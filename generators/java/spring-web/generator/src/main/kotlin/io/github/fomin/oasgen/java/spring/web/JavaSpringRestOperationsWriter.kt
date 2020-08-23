package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.annotated.ConverterMatcherProvider
import io.github.fomin.oasgen.java.dto.jackson.annotated.ConverterRegistry
import io.github.fomin.oasgen.java.dto.jackson.annotated.JavaDtoWriter
import java.util.*

class JavaSpringRestOperationsWriter(
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
            val clientClassName = toJavaClassName(basePackage, openApiSchema, "client")
            val filePath = getFilePath(clientClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import java.net.URI;",
                    "import java.util.Collections;",
                    "import java.util.HashMap;",
                    "import java.util.Map;",
                    "import javax.annotation.Nonnull;",
                    "import javax.annotation.Nullable;",
                    "import org.springframework.http.MediaType;",
                    "import org.springframework.http.RequestEntity;",
                    "import org.springframework.http.ResponseEntity;",
                    "import org.springframework.web.client.RestOperations;",
                    "import org.springframework.web.util.UriComponentsBuilder;"
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
                    val responseSchema: JsonSchema?
                    val responseType: String
                    if (responseEntry != null) {
                        responseSchema = responseEntry.value.schema()
                        responseType = converterRegistry[responseSchema].valueType()
                    } else {
                        responseSchema = null
                        responseType = "java.lang.Void"
                    }

                    val requestBody = operation.requestBody()
                    val bodySchema: JsonSchema?
                    val pathParametersSchemas = mutableSetOf<JsonSchema>()
                    val queryParametersSchemas = mutableSetOf<JsonSchema>()
                    val requestBodyArgDeclaration: String?
                    val requestBodyInternalArgDeclaration: String?
                    val requestBodyArg: String?
                    val requestType: String
                    val buildRequestExpression: String

                    if (requestBody != null) {
                        val entry = requestBody.content().entries.single()
                        bodySchema = entry.value.schema()
                        requestType = converterRegistry[bodySchema].valueType()
                        requestBodyArgDeclaration = "@Nonnull $requestType ${toVariableName(getSimpleName(requestType))}"
                        requestBodyInternalArgDeclaration = "$requestType bodyArg"
                        requestBodyArg = toVariableName(getSimpleName(requestType))
                        buildRequestExpression =
                                """|.contentType(MediaType.APPLICATION_JSON)
                                   |.body(bodyArg, ${requestType}.class);
                                   |""".trimMargin()
                    } else {
                        bodySchema = null
                        requestBodyArgDeclaration = null
                        requestType = "java.lang.Void"
                        requestBodyInternalArgDeclaration = null
                        requestBodyArg = null
                        buildRequestExpression = ".build();"
                    }

                    val parameterArgDeclarations = operation.parameters().map { parameter ->
                        val nullAnnotation = when (parameter.required) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        val parameterType = converterRegistry[parameter.schema()].valueType()
                        """$nullAnnotation $parameterType ${toVariableName(parameter.name)}"""
                    }
                    val parameterInternalArgDeclarations = operation.parameters().mapIndexed { index, parameter ->
                        val parameterType = converterRegistry[parameter.schema()].valueType()
                        """$parameterType param$index"""
                    }
                    val parameterArgs = operation.parameters().map { parameter ->
                        toVariableName(parameter.name)
                    }
                    val methodArgDeclarations = (parameterArgDeclarations + requestBodyArgDeclaration)
                            .filterNotNull().joinToString(",\n")
                    val methodInternalArgDeclarations = (parameterInternalArgDeclarations + requestBodyInternalArgDeclaration)
                            .filterNotNull().joinToString(",\n")
                    val methodArgs = (parameterArgs + requestBodyArg)
                            .filterNotNull().joinToString(",\n")

                    val pathParameterEntries = operation.parameters().mapIndexedNotNull { index, parameter ->
                        if (parameter.parameterIn == ParameterIn.PATH) {
                            val suffix = when (converterRegistry[parameter.schema()].valueType()) {
                                "java.lang.String" -> ""
                                else -> {
                                    pathParametersSchemas.add(parameter.schema())
                                    ".strValue"
                                }
                            }
                            "uriVariables.put(\"${parameter.name}\", param${index}${suffix});"
                        } else {
                            null
                        }
                    }
                    val uriVariablesBlock = if (pathParameterEntries.isEmpty()) {
                        "Map<String, Object> uriVariables = Collections.emptyMap();"
                    } else {
                        """|Map<String, Object> uriVariables = new HashMap<>();
                           |${pathParameterEntries.indentWithMargin(0)}
                        """.trimMargin()
                    }

                    val queryParameterCalls = operation.parameters().mapIndexedNotNull { index, parameter ->
                        if (parameter.parameterIn == ParameterIn.QUERY) {
                            val suffix = when (converterRegistry[parameter.schema()].valueType()) {
                                "java.lang.String" -> ""
                                else -> {
                                    pathParametersSchemas.add(parameter.schema())
                                    ".strValue"
                                }
                            }
                            """.queryParam("${parameter.name}", param${index}${suffix})"""
                        } else
                            null
                    }
                    val methodName = toMethodName(operation.operationId)
                    val methodContent =
                            """|@Nonnull
                               |public ResponseEntity<$responseType> $methodName(
                               |        ${methodArgDeclarations.indentWithMargin(2)}
                               |) {
                               |    return $methodName$0(
                               |            ${methodArgs.indentWithMargin(2)}
                               |    );
                               |}
                               |
                               |private ResponseEntity<$responseType> $methodName$0(
                               |        ${methodInternalArgDeclarations.indentWithMargin(2)}
                               |) {
                               |    ${uriVariablesBlock.indentWithMargin(1)}
                               |    URI uri = UriComponentsBuilder
                               |            .fromUriString(baseUrl + "$pathTemplate")
                               |            ${queryParameterCalls.indentWithMargin(3)}
                               |            .build(uriVariables);
                               |    RequestEntity<$requestType> request = RequestEntity
                               |            .${operation.operationType.name.toLowerCase()}(uri)
                               |            ${buildRequestExpression.indentWithMargin(3)}
                               |    return restOperations.exchange(request, $responseType.class);
                               |}
                               |""".trimMargin()
                    val dtoSchemas = listOfNotNull(
                            bodySchema,
                            responseSchema
                    ) + queryParametersSchemas + pathParametersSchemas
                    OperationOutput(methodContent, dtoSchemas)
                }
            }

            val methodContentList = methodOutputs.map { it.methodContent }
            val dtoSchemas = methodOutputs.flatMap { it.dtoSchemas }

            val content = """
               |package ${getPackage(clientClassName)};
               |
               |${importDeclarations.indentWithMargin(0)}
               |
               |public class ${getSimpleName(clientClassName)} {
               |    private final RestOperations restOperations;
               |    private final String baseUrl;
               |
               |    public ${getSimpleName(clientClassName)}(@Nonnull RestOperations restOperations, @Nonnull String baseUrl) {
               |        this.restOperations = restOperations;
               |        this.baseUrl = baseUrl;
               |    }
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
