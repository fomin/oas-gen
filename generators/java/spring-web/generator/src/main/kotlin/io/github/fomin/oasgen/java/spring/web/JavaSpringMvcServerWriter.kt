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
            val methodContent: String,
            val dtoSchemas: List<JsonSchema>
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
                    val requestBodyArg: String?
                    val consumesPart: String

                    if (requestBody != null) {
                        val entry = requestBody.content().entries.single()
                        bodySchema = entry.value.schema()
                        consumesPart = """, consumes = "${entry.key}""""
                        val bodyType = converterRegistry[bodySchema].valueType()
                        requestBodyArg = "@Nonnull @RequestBody $bodyType ${toVariableName(getSimpleName(bodyType))}"
                    } else {
                        bodySchema = null
                        consumesPart = ""
                        requestBodyArg = null
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
                        val variableType = converterRegistry[parameter.schema()].valueType()
                        """$nullAnnotation @$mappingAnnotation("${parameter.name}") $variableType $variableName"""
                    }
                    val methodArgs = (parameterArgs + requestBodyArg).filterNotNull().joinToString(",\n")

                    val methodContent =
                            """|@Nonnull
                               |@$mappingAnnotationName(path = "$pathTemplate"$producesPart$consumesPart)
                               |ResponseEntity<$responseType> ${toMethodName(operation.operationId)}(
                               |        ${methodArgs.indentWithMargin(2)}
                               |);
                               |
                            """.trimMargin()
                    val dtoSchemas = listOfNotNull(
                            bodySchema,
                            responseSchema
                    )
                    OperationOutput(methodContent, dtoSchemas)
                }
            }

            val methodContentList = methodOutputs.map { it.methodContent }
            val dtoSchemas = methodOutputs.flatMap { it.dtoSchemas }

            val content = """
               |package ${getPackage(routesClassName)};
               |
               |${importDeclarations.indentWithMargin(0)}
               |
               |public interface ${getSimpleName(routesClassName)} {
               |
               |    ${methodContentList.indentWithMargin(1)}
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