package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.annotated.ConverterMatcherProvider
import io.github.fomin.oasgen.java.dto.jackson.annotated.ConverterRegistry
import io.github.fomin.oasgen.java.dto.jackson.annotated.JavaDtoWriter
import java.util.*

class JavaSpringMvcServerWriter(
        private val basePackage: String,
        private val converterIds: List<String>
) : Writer<OpenApiSchema> {
    private data class OperationOutput(
            val controllerMethodContent: String,
            val interfaceMethodContent: String,
            val dtoSchemas: List<JsonSchema>
    )

    private data class OperationArg(
            val name: String,
            val mappingAnnotation: String,
            val nullabilityAnnotation: String,
            val interfaceType: String,
            val controllerType: String,
            val operationCallExpression: String
    )

    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterMatcher = ConverterMatcherProvider.provide(basePackage, converterIds)
        val converterRegistry = ConverterRegistry(converterMatcher)
        val javaDtoWriter = JavaDtoWriter(converterRegistry)
        items.forEach { openApiSchema ->
            val routesClassName = toJavaClassName(basePackage, openApiSchema, "routes")
            val filePath = getFilePath(routesClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import javax.annotation.Nonnull;",
                    "import javax.annotation.Nullable;",
                    "import org.springframework.http.ResponseEntity;",
                    "import org.springframework.stereotype.Controller;",
                    "import org.springframework.web.bind.annotation.*;"
            ))

            val methodOutputs = openApiSchema.paths().pathItems().flatMap { (pathTemplate, pathItem) ->
                pathItem.operations().map { operation ->
                    val response200 = operation.responses().byCode()[HttpResponseCode.CODE_200]
                    val responseEntry = response200?.let { response ->
                        val entries = response.content().entries
                        if (entries.isEmpty()) {
                            null
                        } else {
                            entries.single()
                        }
                    }
                    val producesPart: String
                    val responseSchema: JsonSchema?
                    val responseType: String
                    if (responseEntry != null) {
                        producesPart = """, produces = "${responseEntry.key}""""
                        responseSchema = responseEntry.value.schema()
                        responseType = converterRegistry[responseSchema].valueType()
                    } else {
                        producesPart = ""
                        responseSchema = null
                        responseType = "java.lang.Void"
                    }

                    val mappingAnnotationName = when (operation.operationType) {
                        OperationType.GET -> "GetMapping"
                        OperationType.DELETE -> "DeleteMapping"
                        OperationType.POST -> "PostMapping"
                        OperationType.PUT -> "PutMapping"
                    }

                    val requestBody = operation.requestBody()
                    val bodySchema: JsonSchema?
                    val bodyArg: OperationArg?
                    val consumesPart: String

                    if (requestBody != null) {
                        val entry = requestBody.content().entries.single()
                        bodySchema = entry.value.schema()
                        consumesPart = """, consumes = "${entry.key}""""
                        val bodyType = converterRegistry[bodySchema].valueType()
                        val name = toVariableName(getSimpleName(bodyType))
                        bodyArg = OperationArg(
                                name,
                                "@RequestBody",
                                "@Nonnull",
                                bodyType,
                                bodyType,
                                name
                        )
                    } else {
                        bodySchema = null
                        consumesPart = ""
                        bodyArg = null
                    }

                    val parameterArgs = operation.parameters().map { parameter ->
                        val mappingAnnotation = when (parameter.parameterIn) {
                            ParameterIn.PATH -> "PathVariable"
                            ParameterIn.QUERY -> "RequestParam"
                        }
                        val nullAnnotation = when (parameter.required) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        val variableName = toVariableName(parameter.name)
                        val converter = converterRegistry[parameter.schema()]
                        val variableType = converter.valueType()
                        OperationArg(
                                variableName,
                                """@$mappingAnnotation("${parameter.name}")""",
                                nullAnnotation,
                                variableType,
                                "java.lang.String",
                                "$variableName != null ? ${converter.stringParseExpression(variableName)} : null"
                        )
                    }
                    val operationArgs = (parameterArgs + bodyArg).filterNotNull()
                    val controllerMethodArgs = operationArgs.joinToString(",\n") {
                        val annotations = listOf(it.nullabilityAnnotation, it.mappingAnnotation).joinToString(" ")
                        "$annotations ${it.controllerType} ${it.name}"
                    }
                    val interfaceMethodArgs = operationArgs.joinToString(",\n") {
                        "${it.nullabilityAnnotation} ${it.interfaceType} ${it.name}"
                    }
                    val interfaceCallArgs = operationArgs.joinToString(",\n") {
                        it.operationCallExpression
                    }

                    val methodName = toMethodName(operation.operationId)
                    val controllerMethodContent =
                            """|@$mappingAnnotationName(path = "$pathTemplate"$producesPart$consumesPart)
                               |public ResponseEntity<$responseType> $methodName(
                               |        ${controllerMethodArgs.indentWithMargin(2)}
                               |) {
                               |    return this.operations.$methodName(
                               |            ${interfaceCallArgs.indentWithMargin(3)}
                               |    );
                               |}
                               |
                            """.trimMargin()
                    val interfaceMethodContent =
                            """|ResponseEntity<$responseType> $methodName(
                               |        ${interfaceMethodArgs.indentWithMargin(2)}
                               |);
                               |
                            """.trimMargin()
                    val dtoSchemas = listOfNotNull(
                            bodySchema,
                            responseSchema
                    ) + operation.parameters().map { it.schema() }
                    OperationOutput(controllerMethodContent, interfaceMethodContent, dtoSchemas)
                }
            }

            val controllerMethodContentList = methodOutputs.map { it.controllerMethodContent }
            val interfaceMethodContentList = methodOutputs.map { it.interfaceMethodContent }
            val dtoSchemas = methodOutputs.flatMap { it.dtoSchemas }

            val content = """
               |package ${getPackage(routesClassName)};
               |
               |${importDeclarations.indentWithMargin(0)}
               |
               |@Controller
               |@RequestMapping("${'$'}{$routesClassName.path}")
               |public class ${getSimpleName(routesClassName)} {
               |    public interface Operations {
               |        ${interfaceMethodContentList.indentWithMargin(2)}
               |    }
               |
               |    public final Operations operations;
               |
               |    public ${getSimpleName(routesClassName)}(Operations operations) {
               |        this.operations = operations;
               |    }
               |
               |    ${controllerMethodContentList.indentWithMargin(1)}
               |
               |}
               |
            """.trimMargin()

            val dtoFiles = javaDtoWriter.write(dtoSchemas)
            outputFiles.addAll(dtoFiles)
            outputFiles.add(OutputFile(filePath, content))
        }

        return outputFiles
    }
}