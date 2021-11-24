package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.generator.BodyType
import io.github.fomin.oasgen.generator.bodyType
import io.github.fomin.oasgen.generator.response2xx
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.*

class ReactorNettyClientWriter(
    private val dtoPackage: String,
    private val routePackage: String,
    private val converterIds: List<String>
) : Writer<OpenApiSchema> {

    data class OperationItem(
        val methodDeclaration: String,
        val dtoSchemas: List<JsonSchema>
    )

    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterMatcher = ConverterMatcherProvider.provide(dtoPackage, routePackage, converterIds)
        val converterRegistry = ConverterRegistry(converterMatcher)
        val javaDtoWriter = JavaDtoWriter(converterRegistry)

        items.forEach { openApiSchema ->
            val clientClassName = toJavaClassName(routePackage, openApiSchema, "Client")
            val filePath = getFilePath(clientClassName)

            val operationMethods = openApiSchema.paths().pathItems().flatMap { (pathTemplate, pathItem) ->
                pathItem.operations().map { operation ->
                    val (responseCode, response) = response2xx(operation.responses())
                    val requestBodyType = bodyType(operation.requestBody()?.content())
                    val responseBodyType = bodyType(response.content())

                    val returnType = when (responseBodyType) {
                        null -> "Mono<java.lang.Void>"
                        is BodyType.Json -> {
                            val valueType = converterRegistry[responseBodyType.jsonSchema].valueType()
                            "Mono<$valueType>"
                        }
                        is BodyType.Binary -> "Flux<ByteBuf>"
                    }

                    val methodName = toMethodName(operation.operationId)

                    data class MethodArg(
                        val publicDeclaration: String,
                        val privateDeclaration: String,
                        val publicName: String,
                    )

                    val requestBodyArg = when (requestBodyType) {
                        null -> null
                        is BodyType.Json -> {
                            val valueType = converterRegistry[requestBodyType.jsonSchema].valueType()
                            val variableName = toVariableName(getSimpleName(valueType))
                            MethodArg(
                                "@Nonnull Mono<$valueType> $variableName",
                                "Mono<${valueType}> bodyArg",
                                variableName,
                            )
                        }
                        is BodyType.Binary -> {
                            MethodArg(
                                "@Nonnull Flux<ByteBuf> requestBodyFlux",
                                "@Nonnull Flux<ByteBuf> requestBodyFlux",
                                "requestBodyFlux",
                            )
                        }
                    }
                    val parameterArgs = operation.parameters().mapIndexed { index, parameter ->
                        val annotation = when (parameter.required) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        val valueType = converterRegistry[parameter.schema()].valueType()
                        MethodArg(
                            """$annotation $valueType ${toVariableName(parameter.name)}""",
                            """$valueType param$index""",
                            toVariableName(parameter.name)
                        )
                    }
                    val args = parameterArgs.plus(requestBodyArg).filterNotNull()
                    val headerParameters = operation.parameters().mapIndexedNotNull { index, parameter ->
                        if (parameter.parameterIn == ParameterIn.HEADER)
                            """|if (param${index}Str != null) {
                               |    headers.set("${parameter.name}", param${index}Str);
                               |}
                            """.trimMargin()
                        else {
                            null
                        }
                    }
                    val contentTypeHeader = when (requestBodyType) {
                        null -> null
                        else -> """headers.set("Content-Type", "${requestBodyType.contentType}");"""
                    }
                    val requestHeaders = headerParameters.plus(contentTypeHeader).filterNotNull()
                    val headersCall = if (requestHeaders.isNotEmpty()) {
                        """|.headers(headers -> {
                           |    ${requestHeaders.indentWithMargin(1)}
                           |})
                        """.trimMargin()
                    } else {
                        ""
                    }
                    val queryParameterArgs = operation.parameters().mapIndexedNotNull { index, parameter ->
                        if (parameter.parameterIn == ParameterIn.QUERY) {
                            """, "${toVariableName(parameter.name)}", param${index}Str"""
                        } else {
                            null
                        }
                    }.joinToString("")
                    val parameterStrDeclarations = operation.parameters().mapIndexed { index, parameter ->
                        val stringWriteExpression =
                            converterRegistry[parameter.schema()].stringWriteExpression("param$index")
                        "String param${index}Str = param$index != null ? $stringWriteExpression : null;"
                    }
                    val sendCall = when (requestBodyType) {
                        null -> ""
                        is BodyType.Json -> {
                            val writeExpression = converterRegistry[requestBodyType.jsonSchema].writeExpression(
                                "jsonGenerator",
                                "value"
                            )
                            """|.send((httpClientRequest, nettyOutbound) -> {
                               |    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, (jsonGenerator, value) -> $writeExpression);
                               |    return nettyOutbound.send(byteBufMono);
                               |})
                            """.trimMargin()
                        }
                        is BodyType.Binary ->
                            """|.send((httpClientRequest, nettyOutbound) -> {
                               |    return nettyOutbound.send(requestBodyFlux);
                               |})
                            """.trimMargin()
                    }
                    val responseCall = when (responseBodyType) {
                        null ->
                            """|.response()
                               |.handle((httpClientResponse, sink) -> {
                               |    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                               |    if (httpResponseStatus.code() == $responseCode) {
                               |        sink.complete();
                               |    } else {
                               |        sink.error(new RuntimeException(httpResponseStatus.toString()));
                               |    }
                               |})
                            """.trimMargin()
                        is BodyType.Json -> {
                            val parseExpression = converterRegistry[responseBodyType.jsonSchema].parseExpression(
                                "jsonNode"
                            )
                            """|.responseSingle((httpClientResponse, byteBufMono) -> {
                               |    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                               |    if (httpResponseStatus.code() == $responseCode) {
                               |        return byteBufConverter.parse(byteBufMono, jsonNode -> $parseExpression);
                               |    } else {
                               |        return Mono.error(new RuntimeException(httpResponseStatus.toString()));
                               |    }
                               |})
                            """.trimMargin()
                        }
                        is BodyType.Binary ->
                            """|.response((httpClientResponse, byteBufFlux) -> {
                               |    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                               |    if (httpResponseStatus.code() == $responseCode) {
                               |        return byteBufFlux;
                               |    } else {
                               |        return Flux.error(new RuntimeException(httpResponseStatus.toString()));
                               |    }
                               |})
                            """.trimMargin()
                    }

                    val url = pathTemplateToUrl(pathTemplate, operation.parameters())
                    val content =
                        """|@Nonnull
                           |public $returnType $methodName(
                           |        ${args.joinToString(",\n") { it.publicDeclaration }.indentWithMargin(2)}
                           |) {
                           |    return $methodName$0(
                           |            ${args.joinToString(",\n") { it.publicName }.indentWithMargin(3)}
                           |    );
                           |}
                           |
                           |private $returnType $methodName$0(
                           |        ${args.joinToString(",\n") { it.privateDeclaration }.indentWithMargin(2)}
                           |) {
                           |    ${parameterStrDeclarations.indentWithMargin(1)}
                           |    return httpClient
                           |            ${headersCall.indentWithMargin(3)}
                           |            .${operation.operationType.name.lowercase()}()
                           |            .uri(UrlEncoderUtils.encodeUrl($url$queryParameterArgs))
                           |            ${sendCall.indentWithMargin(3)}
                           |            ${responseCall.indentWithMargin(3)};
                           |}
                           |
                        """.trimMargin()
                    val dtoSchemas = operation.parameters().map { it.schema() }
                        .plus(requestBodyType?.jsonSchema())
                        .plus(responseBodyType?.jsonSchema())
                        .filterNotNull()
                    OperationItem(content, dtoSchemas)
                }
            }

            val content = """
               |package ${getPackage(clientClassName)};
               |
               |import com.fasterxml.jackson.databind.ObjectMapper;
               |import io.github.fomin.oasgen.ByteBufConverter;
               |import io.github.fomin.oasgen.UrlEncoderUtils;
               |import io.netty.buffer.ByteBuf;
               |import javax.annotation.Nonnull;
               |import javax.annotation.Nullable;
               |import io.netty.handler.codec.http.HttpResponseStatus;
               |import reactor.core.publisher.Flux;
               |import reactor.core.publisher.Mono;
               |import reactor.netty.http.client.HttpClient;
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
               |    ${operationMethods.map { it.methodDeclaration }.indentWithMargin(1)}
               |
               |}
               |
            """.trimMargin()

            val dtoSchemas = operationMethods.flatMap { it.dtoSchemas }
            val dtoFiles = javaDtoWriter.write(dtoSchemas)
            outputFiles.addAll(dtoFiles)
            outputFiles.add(OutputFile(filePath, content, OutputFileType.ROUTE))
        }

        return outputFiles
    }

    private fun pathTemplateToUrl(pathTemplate: String, parameters: List<Parameter>): String {
        val parametersMap = parameters.mapIndexed { index, parameter -> parameter.name to index }.toMap()
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
