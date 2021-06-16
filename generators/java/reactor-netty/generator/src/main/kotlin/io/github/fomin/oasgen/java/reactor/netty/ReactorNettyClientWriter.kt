package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.*
import java.util.*

class ReactorNettyClientWriter(
        private val dtoPackage: String,
        private val routePackage: String,
        private val converterIds: List<String>
) : Writer<OpenApiSchema> {
    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterMatcher = ConverterMatcherProvider.provide(dtoPackage, routePackage, converterIds)
        val converterRegistry = ConverterRegistry(converterMatcher)
        val javaDtoWriter = JavaDtoWriter(converterRegistry)

        items.forEach { openApiSchema ->
            val clientClassName = toJavaClassName(routePackage, openApiSchema, "Client")
            val filePath = getFilePath(clientClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import com.fasterxml.jackson.databind.ObjectMapper;",
                    "import io.github.fomin.oasgen.ByteBufConverter;",
                    "import io.github.fomin.oasgen.UrlEncoderUtils;",
                    "import io.netty.buffer.ByteBuf;",
                    "import javax.annotation.Nonnull;",
                    "import javax.annotation.Nullable;",
                    "import reactor.core.publisher.Mono;",
                    "import reactor.netty.http.client.HttpClient;"
            ))

            val paths = openApiSchema.paths()
            val javaOperations = toJavaOperations(converterRegistry, paths)

            val operationMethods = javaOperations.map { javaOperation ->
                val requestBodyArgDeclaration = javaOperation.requestVariable?.let { requestVariable ->
                    "@Nonnull Mono<${requestVariable.type}> ${toVariableName(getSimpleName(requestVariable.type))}"
                }
                val requestBodyInternalArgDeclaration = javaOperation.requestVariable?.let { requestVariable ->
                    "Mono<${requestVariable.type}> bodyArg"
                }
                val requestBodyArg = javaOperation.requestVariable?.let { requestVariable ->
                    toVariableName(getSimpleName(requestVariable.type))
                }
                val parameterArgDeclarations = javaOperation.parameters.map { javaParameter ->
                    val annotation = when (javaParameter.schemaParameter.required) {
                        true -> "@Nonnull"
                        false -> "@Nullable"
                    }
                    """$annotation ${javaParameter.javaVariable.type} ${javaParameter.javaVariable.name}"""
                }
                val parameterInternalArgDeclarations = javaOperation.parameters.mapIndexed { index, javaParameter ->
                    """${javaParameter.javaVariable.type} param$index"""
                }
                val parameterArgs = javaOperation.parameters.map { javaParameter ->
                    javaParameter.javaVariable.name
                }
                val methodArgDeclarations = (parameterArgDeclarations + requestBodyArgDeclaration)
                        .filterNotNull().joinToString(",\n")
                val methodInternalArgDeclarations = (parameterInternalArgDeclarations + requestBodyInternalArgDeclaration)
                        .filterNotNull().joinToString(",\n")
                val methodArgs = (parameterArgs + requestBodyArg)
                        .filterNotNull().joinToString(",\n")
                val responseType = javaOperation.responseVariable.type ?: "java.lang.Void"

                val responseCall = when (val responseSchema = javaOperation.responseVariable.schema) {
                    null -> ".response()"
                    else ->
                        """|.responseSingle((httpClientResponse, byteBufMono) ->
                           |        byteBufConverter.parse(byteBufMono, jsonNode -> ${converterRegistry[responseSchema].parseExpression("jsonNode")})
                           |)""".trimMargin()
                }

                val sendCall = when (val requestSchema = javaOperation.requestVariable?.schema) {
                    null -> ""
                    else -> """|.send((httpClientRequest, nettyOutbound) -> {
                               |    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, (jsonGenerator, value) -> ${converterRegistry[requestSchema].writeExpression("jsonGenerator", "value")});
                               |    return nettyOutbound.send(byteBufMono);
                               |})
                               |""".trimMargin()
                }

                val queryParameterArgs = javaOperation.parameters.mapIndexedNotNull { index, javaParameter ->
                    if (javaParameter.schemaParameter.parameterIn == ParameterIn.QUERY) {
                        """, "${javaParameter.name}", param${index}Str"""
                    } else {
                        null
                    }
                }.joinToString("")

                val parameterStrDeclarations = javaOperation.parameters.mapIndexedNotNull { index, javaParameter ->
                            if (javaParameter.schemaParameter.parameterIn != ParameterIn.HEADER) {
                                val stringWriteExpression =
                                        converterRegistry[javaParameter.schemaParameter.schema()].stringWriteExpression("param$index")
                                "String param${index}Str = param$index != null ? $stringWriteExpression : null;"
                            } else {
                                null
                            }
                }

                val headerArgs = javaOperation.parameters
                        .mapIndexedNotNull { index, parameter ->
                            if (parameter.schemaParameter.parameterIn == ParameterIn.HEADER) {
                                val stringWriteExpression =
                                        converterRegistry[parameter.schemaParameter.schema()].stringWriteExpression("param$index")
                                """.add("${parameter.name}", param$index != null ? $stringWriteExpression : null)"""
                            } else {
                                null
                            }
                        }
                val headerArgsBlock = when(headerArgs.isNotEmpty()) {
                    true -> """|        .headers(headers -> headers${headerArgs.indentWithMargin(5)})""".trimMargin()
                    false -> ""
                }

                """|@Nonnull
                   |public Mono<$responseType> ${javaOperation.methodName}(
                   |        ${methodArgDeclarations.indentWithMargin(2)}
                   |) {
                   |    return ${javaOperation.methodName}$0(
                   |            ${methodArgs.indentWithMargin(3)}
                   |    );
                   |}
                   |
                   |private Mono<$responseType> ${javaOperation.methodName}$0(
                   |        ${methodInternalArgDeclarations.indentWithMargin(2)}
                   |) {
                   |    ${parameterStrDeclarations.indentWithMargin(1)}
                   |    return httpClient
                   |    $headerArgsBlock
                   |            .${javaOperation.operation.operationType.name.toLowerCase()}()
                   |            .uri(UrlEncoderUtils.encodeUrl(${pathTemplateToUrl(javaOperation.pathTemplate, javaOperation.parameters)}$queryParameterArgs))
                   |            ${sendCall.indentWithMargin(3)}
                   |            ${responseCall.indentWithMargin(3)};
                   |}
                   |""".trimMargin()
            }

            val content = """
               |package ${getPackage(clientClassName)};
               |
               |${importDeclarations.indentWithMargin(0)}
               |
               |public class ${getSimpleName(clientClassName)} {
               |    private final ByteBufConverter byteBufConverter;
               |    private final HttpClient httpClient;
               |
               |    public ${getSimpleName(clientClassName)}(@Nonnull ObjectMapper objectMapper, @Nonnull HttpClient httpClient) {
               |        this.byteBufConverter = new ByteBufConverter(objectMapper);
               |        this.httpClient = httpClient;
               |    }
               |
               |    ${operationMethods.indentWithMargin(1)}
               |
               |}
               |
            """.trimMargin()

            val dtoSchemas = mutableListOf<JsonSchema>()
            dtoSchemas.addAll(javaOperations.mapNotNull { it.responseVariable.schema })
            dtoSchemas.addAll(javaOperations.mapNotNull { it.requestVariable?.schema })
            dtoSchemas.addAll(javaOperations.flatMap { javaOperation ->
                javaOperation.parameters.map { it.schemaParameter.schema() }
            })
            val dtoFiles = javaDtoWriter.write(dtoSchemas)
            outputFiles.addAll(dtoFiles)
            outputFiles.add(OutputFile(filePath, content, OutputFileType.ROUTE))
        }

        return outputFiles
    }

    private fun pathTemplateToUrl(pathTemplate: String, parameters: List<JavaParameter>): String {
        val parametersMap = parameters.map { it.name to it.index }.toMap()
        val expressionParts = mutableListOf<String>()
        var openBraceIndex = -1
        var closeBraceIndex = -1
        do {
            openBraceIndex = pathTemplate.indexOf('{', openBraceIndex + 1)
            if (openBraceIndex > 0) {
                expressionParts.quoteAndAdd(pathTemplate.substring(closeBraceIndex + 1, openBraceIndex))
                closeBraceIndex = pathTemplate.indexOf('}', closeBraceIndex + 1)
                val paramName = pathTemplate.substring(openBraceIndex + 1, closeBraceIndex)
                if (closeBraceIndex > 0) {
                    expressionParts.add("UrlEncoderUtils.encode(param${parametersMap[paramName]}Str)")
                } else {
                    expressionParts.quoteAndAdd(pathTemplate.substring(openBraceIndex + 1))
                }
            } else {
                expressionParts.quoteAndAdd(pathTemplate.substring(closeBraceIndex + 1))
            }
        } while (openBraceIndex > 0)
        return expressionParts.joinToString(" + ")
    }

    private fun MutableList<String>.quoteAndAdd(str: String) {
        if (str.isNotEmpty()) {
            this.add("\"$str\"")
        }
    }
}
