package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.jackson.*
import java.util.*

class ReactorNettyClientWriter(
        private val basePackage: String
) : Writer<OpenApiSchema> {
    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterRegistry = ConverterRegistry(listOf(
                ZonedDateTimeConverterMatcher(),
                LocalDateConverterMatcher(),
                LocalDateTimeConverterMatcher(),
                ArrayConverterMatcher(),
                MapConverterMatcher(),
                ObjectConverterMatcher(basePackage),
                Int32ConverterMatcher(),
                Int64ConverterMatcher(),
                IntegerConverterMatcher(),
                NumberConverterMatcher(),
                BooleanConverterMatcher(),
                EnumConverterMatcher(basePackage),
                StringConverterMatcher()
        ))

        val javaDtoWriter = JavaDtoWriter(converterRegistry)

        items.forEach { openApiSchema ->
            val clientClassName = toJavaClassName(basePackage, openApiSchema, "Client")
            val filePath = getFilePath(clientClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import com.fasterxml.jackson.core.JsonFactory;",
                    "import io.github.fomin.oasgen.ByteBufConverter;",
                    "import io.github.fomin.oasgen.UrlEncoderUtils;",
                    "import io.netty.buffer.ByteBuf;",
                    "import reactor.core.publisher.Flux;",
                    "import reactor.core.publisher.Mono;",
                    "import reactor.netty.http.client.HttpClient;"
            ))

            val paths = openApiSchema.paths()
            val javaOperations = toJavaOperations(converterRegistry, paths)

            val operationMethods = javaOperations.map { javaOperation ->
                val requestBodyArgDeclaration = javaOperation.requestVariable?.let { requestVariable ->
                    "Mono<${requestVariable.type}> ${toVariableName(getSimpleName(requestVariable.type))}"
                }
                val requestBodyInternalArgDeclaration = javaOperation.requestVariable?.let { requestVariable ->
                    "Mono<${requestVariable.type}> bodyArg"
                }
                val requestBodyArg = javaOperation.requestVariable?.let { requestVariable ->
                    toVariableName(getSimpleName(requestVariable.type))
                }
                val parameterArgDeclarations = javaOperation.parameters.map { javaParameter ->
                    """${javaParameter.javaVariable.type} ${javaParameter.javaVariable.name}"""
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

                val returnExpression = when (val responseSchema = javaOperation.responseVariable.schema) {
                    null -> "byteFlux.thenEmpty()"
                    else -> "byteBufConverter.parse(responseByteBufFlux, ${converterRegistry[responseSchema].parserCreateExpression()})"
                }

                val sendCall = when (val requestSchema = javaOperation.requestVariable?.schema) {
                    null -> ""
                    else -> """|.send((httpClientRequest, nettyOutbound) -> {
                               |    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, ${converterRegistry[requestSchema].writerCreateExpression()});
                               |    return nettyOutbound.send(byteBufMono);
                               |})
                               |""".trimMargin()
                }

                val queryParameterArgs = javaOperation.parameters.mapIndexedNotNull { index, javaParameter ->
                    if (javaParameter.schemaParameter.parameterIn == ParameterIn.QUERY) {
                        """, "${javaParameter.name}", param$index"""
                    } else {
                        null
                    }
                }.joinToString("")

                """|public Mono<$responseType> ${javaOperation.methodName}(
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
                   |    Flux<ByteBuf> responseByteBufFlux = httpClient
                   |            .${javaOperation.operation.operationType.name.toLowerCase()}()
                   |            .uri(UrlEncoderUtils.encodeUrl(${pathTemplateToUrl(javaOperation.pathTemplate)}$queryParameterArgs))
                   |            ${sendCall.indentWithMargin(3)}
                   |            .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
                   |    return ${returnExpression};
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
               |    public ${getSimpleName(clientClassName)}(JsonFactory jsonFactory, HttpClient httpClient) {
               |        this.byteBufConverter = new ByteBufConverter(jsonFactory);
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
            val dtoFiles = javaDtoWriter.write(dtoSchemas)
            outputFiles.addAll(dtoFiles)
            outputFiles.add(OutputFile(filePath, content))
        }

        return outputFiles
    }

    private fun pathTemplateToUrl(pathTemplate: String): String {
        val expressionParts = mutableListOf<String>()
        var openBraceIndex = -1
        var closeBraceIndex = -1
        var parameterIndex = 0
        do {
            openBraceIndex = pathTemplate.indexOf('{', openBraceIndex + 1)
            if (openBraceIndex > 0) {
                expressionParts.quoteAndAdd(pathTemplate.substring(closeBraceIndex + 1, openBraceIndex))
                closeBraceIndex = pathTemplate.indexOf('}', closeBraceIndex + 1)
                if (closeBraceIndex > 0) {
                    expressionParts.add("UrlEncoderUtils.encode(param$parameterIndex)")
                    parameterIndex += 1
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
