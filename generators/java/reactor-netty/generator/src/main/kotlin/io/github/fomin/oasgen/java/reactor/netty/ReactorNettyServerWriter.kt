package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.generator.BodyType
import io.github.fomin.oasgen.generator.bodyType
import io.github.fomin.oasgen.generator.response2xx
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterMatcherProvider
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterRegistry
import io.github.fomin.oasgen.java.dto.jackson.wstatic.JavaDtoWriter

class ReactorNettyServerWriter(
    private val dtoPackage: String,
    private val routePackage: String,
    private val converterIds: List<String>
) : Writer<OpenApiSchema> {

    data class OperationItem(
        val methodDeclaration: String,
        val routeHandler: String,
        val dtoSchemas: List<JsonSchema>
    )

    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterMatcher = ConverterMatcherProvider.provide(dtoPackage, routePackage, converterIds)
        val converterRegistry = ConverterRegistry(converterMatcher)
        val javaDtoWriter = JavaDtoWriter(converterRegistry)

        items.forEach { openApiSchema ->
            val routesClassName = toJavaClassName(routePackage, openApiSchema, "routes")
            val filePath = getFilePath(routesClassName)

            val operationItems = openApiSchema.paths().pathItems().flatMap { (pathTemplate, pathItem) ->
                pathItem.operations().map { operation ->
                    val (responseCode, response) = response2xx(operation.responses())
                    val requestBodyType = bodyType(operation.requestBody()?.content())
                    val responseBodyType = bodyType(response.content())

                    val responseTypeDeclaration = when (responseBodyType) {
                        null -> "Mono<Void>"
                        is BodyType.Json -> "Mono<${converterRegistry[responseBodyType.jsonSchema].valueType()}>"
                        is BodyType.Binary -> "Flux<ByteBuf>"
                    }
                    val methodName = toMethodName(operation.operationId)
                    val parameterArgDeclarations = operation.parameters().map { parameter ->
                        val nullAnnotation = when (parameter.required) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        val parameterType = converterRegistry[parameter.schema()].valueType()
                        "$nullAnnotation $parameterType ${toVariableName(parameter.name)}"
                    }
                    val requestBodyArgDeclaration = when (requestBodyType) {
                        null -> null
                        is BodyType.Json -> {
                            val valueType = converterRegistry[requestBodyType.jsonSchema].valueType()
                            "@Nonnull Mono<$valueType> requestBodyMono"
                        }
                        is BodyType.Binary -> "@Nonnull ByteBufFlux requestBodyFlux"
                    }
                    val argDeclarations = parameterArgDeclarations
                        .plus(requestBodyArgDeclaration)
                        .filterNotNull()
                        .joinToString(", ")

                    val methodDeclaration =
                        """|@Nonnull
                           |public abstract $responseTypeDeclaration ${methodName}($argDeclarations);
                           |
                        """.trimMargin()

                    val queryParamMapDeclaration =
                        if (operation.parameters().any { it.parameterIn == ParameterIn.QUERY })
                            "Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());"
                        else
                            ""
                    val parameterDeclarations = operation.parameters().mapIndexed { index, parameter ->
                        val converterWriter = converterRegistry[parameter.schema()]
                        val javaType = converterWriter.valueType()
                        val parameterStrExpression = when (val parameterIn = parameter.parameterIn) {
                            ParameterIn.PATH ->
                                """request.param("${parameter.name}")"""
                            ParameterIn.QUERY ->
                                """queryParams.get("${parameter.name}")"""
                            ParameterIn.HEADER ->
                                """request.requestHeaders().get("${parameter.name}")"""
                            else -> error("unsupported parameter type $parameterIn")
                        }
                        val stringParseExpression = converterWriter.stringParseExpression("param${index}Str")
                        """|String param${index}Str = $parameterStrExpression;
                           |$javaType param$index = param${index}Str != null ? $stringParseExpression : null;
                        """.trimMargin()
                    }

                    val parameterArgs = operation.parameters().mapIndexed { index, _ -> "param$index" }
                    val requestArg = if (requestBodyType == null) null else "requestPublisher"
                    val args = (parameterArgs + requestArg).filterNotNull().joinToString(", ")
                    val requestPublisher = when (requestBodyType) {
                        null -> ""
                        is BodyType.Json -> {
                            val converterWriter = converterRegistry[requestBodyType.jsonSchema]
                            val valueType = converterWriter.valueType()
                            val parseExpression = converterWriter.parseExpression("jsonNode")
                            """|Mono<$valueType> requestPublisher = byteBufConverter.parse(
                               |        request.receive().aggregate(),
                               |        jsonNode -> $parseExpression
                               |);
                            """.trimMargin()
                        }
                        is BodyType.Binary -> {
                            "ByteBufFlux requestPublisher = request.receive();"
                        }
                    }
                    val responsePublisher = when (responseBodyType) {
                        null -> "Publisher<ByteBuf> responsePublisher = $methodName($args).cast(ByteBuf.class);"
                        is BodyType.Json -> {
                            val writeExpression = converterRegistry[responseBodyType.jsonSchema]
                                .writeExpression("jsonGenerator", "value")
                            """|Publisher<ByteBuf> responsePublisher = byteBufConverter.write(
                               |        response,
                               |        $methodName($args),
                               |        (jsonGenerator, value) -> $writeExpression
                               |);
                            """.trimMargin()
                        }
                        is BodyType.Binary -> "Publisher<ByteBuf> responsePublisher = $methodName($args);"
                    }
                    val responseContentType = if (responseBodyType == null) {
                        ""
                    } else {
                        """.header("Content-Type", "${responseBodyType.contentType}")"""
                    }
                    val sendResponse =
                        """|return response
                           |        .status($responseCode)
                           |        $responseContentType
                           |        .send(responsePublisher);
                        """.trimMargin()
                    val routeMethod = operation.operationType.name.lowercase()
                    val routeHandler =
                        """|.$routeMethod(baseUrl + "$pathTemplate", (request, response) -> {
                           |    $queryParamMapDeclaration
                           |    ${parameterDeclarations.indentWithMargin(1)}
                           |    ${requestPublisher.indentWithMargin(1)}
                           |    ${responsePublisher.indentWithMargin(1)}
                           |    ${sendResponse.indentWithMargin(1)}
                           |})
                        """.trimMargin()
                    OperationItem(
                        methodDeclaration,
                        routeHandler,
                        operation.parameters().map { it.schema() }
                            .plus(requestBodyType?.jsonSchema())
                            .plus(responseBodyType?.jsonSchema())
                            .filterNotNull()
                    )
                }
            }

            val content =
                """|package ${getPackage(routesClassName)};
                   |
                   |import com.fasterxml.jackson.databind.ObjectMapper;
                   |import io.github.fomin.oasgen.ByteBufConverter;
                   |import io.github.fomin.oasgen.UrlEncoderUtils;
                   |import java.util.Map;
                   |import java.util.function.Consumer;
                   |import javax.annotation.Nonnull;
                   |import javax.annotation.Nullable;
                   |import io.netty.buffer.ByteBuf;
                   |import org.reactivestreams.Publisher;
                   |import reactor.core.publisher.Flux;
                   |import reactor.core.publisher.Mono;
                   |import reactor.netty.ByteBufFlux;
                   |import reactor.netty.http.server.HttpServerRoutes;
                   |
                   |public abstract class ${getSimpleName(routesClassName)} implements Consumer<HttpServerRoutes> {
                   |    private final ByteBufConverter byteBufConverter;
                   |    private final String baseUrl;
                   |
                   |    protected ${getSimpleName(routesClassName)}(ObjectMapper objectMapper, String baseUrl) {
                   |        this.byteBufConverter = new ByteBufConverter(objectMapper);
                   |        this.baseUrl = baseUrl;
                   |    }
                   |
                   |    ${operationItems.map { it.methodDeclaration }.indentWithMargin(1)}
                   |
                   |    @Override
                   |    public final void accept(HttpServerRoutes httpServerRoutes) {
                   |        httpServerRoutes
                   |            ${operationItems.map { it.routeHandler }.indentWithMargin(3)}
                   |        ;
                   |    }
                   |}
                   |
                """.trimMargin()
            val routesOutputFile = OutputFile(filePath, content, OutputFileType.ROUTE)
            outputFiles.add(routesOutputFile)

            val dtoSchemas = operationItems.flatMap { operationItem ->
                operationItem.dtoSchemas
            }
            outputFiles.addAll(javaDtoWriter.write(dtoSchemas))
        }

        return outputFiles
    }

}
