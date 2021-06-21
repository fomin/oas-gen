package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterMatcherProvider
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterRegistry
import io.github.fomin.oasgen.java.dto.jackson.wstatic.JavaDtoWriter
import java.util.*

class JavaSpringWebClientWriter(
        private val dtoPackage: String,
        private val routesPackage: String,
        private val converterIds: List<String>
) : Writer<OpenApiSchema> {
    private data class OperationOutput(
            val methodContent: String,
            val dtoSchemas: List<JsonSchema>
    )

    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterMatcher = ConverterMatcherProvider.provide(dtoPackage, routesPackage, converterIds)
        val converterRegistry = ConverterRegistry(converterMatcher)
        val javaDtoWriter = JavaDtoWriter(converterRegistry)
        items.forEach { openApiSchema ->
            val clientClassName = toJavaClassName(routesPackage, openApiSchema, "client")
            val filePath = getFilePath(clientClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import io.github.fomin.oasgen.SpringMvcClient;",
                    "import java.net.URI;",
                    "import java.util.Collections;",
                    "import java.util.HashMap;",
                    "import java.util.Map;",
                    "import javax.annotation.Nonnull;",
                    "import javax.annotation.Nullable;",
                    "import org.springframework.http.HttpMethod;",
                    "import org.springframework.web.util.UriComponentsBuilder;"
            ))

            val methodOutputs = openApiSchema.paths().pathItems().flatMap { (pathTemplate, pathItem) ->
                pathItem.operations().map { operation ->
                    val response200 = operation.responses().singleOrNull2xx()
                    val responseEntry = response200?.value?.let { response ->
                        val entries = response.content().entries
                        if (entries.isEmpty()) {
                            null
                        } else {
                            entries.single()
                        }
                    }
                    val responseSchema: JsonSchema?
                    val responseType: String
                    val responseParserArg: String
                    if (responseEntry != null) {
                        responseSchema = responseEntry.value.schema()
                        responseType = converterRegistry[responseSchema].valueType()
                        responseParserArg = "jsonNode -> ${converterRegistry[responseSchema].parseExpression("jsonNode")}"
                    } else {
                        responseSchema = null
                        responseType = "java.lang.Void"
                        responseParserArg = "null"
                    }

                    val requestBody = operation.requestBody()
                    val bodySchema: JsonSchema?
                    val requestBodyArgDeclaration: String?
                    val requestBodyInternalArgDeclaration: String?
                    val requestBodyArg: String?
                    val requestType: String

                    if (requestBody != null) {
                        val entry = requestBody.content().entries.single()
                        bodySchema = entry.value.schema()
                        requestType = converterRegistry[bodySchema].valueType()
                        requestBodyArgDeclaration = "@Nonnull $requestType ${toVariableName(getSimpleName(requestType))}"
                        requestBodyInternalArgDeclaration = "$requestType bodyArg"
                        requestBodyArg = toVariableName(getSimpleName(requestType))
                    } else {
                        bodySchema = null
                        requestBodyArgDeclaration = null
                        requestBodyInternalArgDeclaration = null
                        requestBodyArg = null
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
                        val stringWriteExpression =
                            converterRegistry[parameter.schema()].stringWriteExpression("param${index}")
                        if (parameter.parameterIn == ParameterIn.PATH) {
                            "uriVariables.put(\"${parameter.name}\", param${index} != null ? $stringWriteExpression : null);"
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

                    val headerEntries = operation.parameters().mapIndexedNotNull { index, parameter ->
                        if (parameter.parameterIn == ParameterIn.HEADER) {
                            val stringWriteExpression =
                                    converterRegistry[parameter.schema()].stringWriteExpression("param${index}")
                            """|if (param${index} != null) {
                               |    headers.set("${parameter.name}", $stringWriteExpression);
                               |}
                            """.trimMargin()
                        } else {
                            null
                        }
                    }

                    val queryParameterCalls = operation.parameters().mapIndexedNotNull { index, parameter ->
                        val stringWriteExpression =
                                converterRegistry[parameter.schema()].stringWriteExpression("param${index}")
                        if (parameter.parameterIn == ParameterIn.QUERY)
                            """.queryParam("${parameter.name}", param${index} != null ? $stringWriteExpression : null)"""
                        else
                            null
                    }
                    val methodName = toMethodName(operation.operationId)
                    val methodContent =
                            """|@Nonnull
                               |public $responseType $methodName(
                               |        ${methodArgDeclarations.indentWithMargin(2)}
                               |) {
                               |    return $methodName$0(
                               |            ${methodArgs.indentWithMargin(3)}
                               |    );
                               |}
                               |
                               |private $responseType $methodName$0(
                               |        ${methodInternalArgDeclarations.indentWithMargin(2)}
                               |) {
                               |    ${uriVariablesBlock.indentWithMargin(1)}
                               |    URI uri = UriComponentsBuilder
                               |            .fromUriString(baseUrl + "$pathTemplate")
                               |            ${queryParameterCalls.indentWithMargin(3)}
                               |            .build(uriVariables);
                               |    return springMvcClient.doRequest(
                               |            uri,
                               |            HttpMethod.${operation.operationType.name},
                               |            ${if (bodySchema != null) "bodyArg" else "null"},
                               |            ${if (bodySchema != null) "(jsonGenerator, value) -> ${converterRegistry[bodySchema].writeExpression("jsonGenerator", "value")}" else "null"},
                               |            $responseParserArg,
                               |            headers -> {
                               |                ${headerEntries.indentWithMargin(4)}
                               |            }
                               |    );
                               |}
                               |""".trimMargin()
                    val dtoSchemas = listOfNotNull(
                            bodySchema,
                            responseSchema
                    ) + operation.parameters().map { it.schema() }
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
               |    private final SpringMvcClient springMvcClient;
               |    private final String baseUrl;
               |
               |    public ${getSimpleName(clientClassName)}(@Nonnull SpringMvcClient springMvcClient, @Nonnull String baseUrl) {
               |        this.springMvcClient = springMvcClient;
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
            outputFiles.add(OutputFile(filePath, content, OutputFileType.ROUTE))
        }

        return outputFiles
    }

}
