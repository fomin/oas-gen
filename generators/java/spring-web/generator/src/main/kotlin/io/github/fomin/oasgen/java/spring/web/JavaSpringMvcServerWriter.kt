package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.generator.BodyType
import io.github.fomin.oasgen.generator.bodyType
import io.github.fomin.oasgen.generator.response2xx
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterMatcherProvider
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterRegistry
import io.github.fomin.oasgen.java.dto.jackson.wstatic.JavaDtoWriter

class JavaSpringMvcServerWriter(
    private val dtoPackage: String,
    private val routesPackage: String,
    private val converterIds: List<String>
) : Writer<OpenApiSchema> {

    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterMatcher = ConverterMatcherProvider.provide(dtoPackage, routesPackage, converterIds)
        val converterRegistry = ConverterRegistry(converterMatcher)
        val javaDtoWriter = JavaDtoWriter(converterRegistry)
        items.forEach { openApiSchema ->
            val dtoSchemas = openApiSchema.paths().pathItems().map { (_, pathItem) ->
                pathItem.operations().map { operation ->
                    val parameterSchemas = operation.parameters().map { parameter ->
                        parameter.schema()
                    }
                    val requestBodySchemas = when (val requestBody = operation.requestBody()) {
                        null -> emptyList()
                        else -> requestBody.content().map { (_, mediaTypeObject) ->
                            mediaTypeObject.schema()
                        }
                    }
                    val responseBodySchemas = operation.responses().byCode().map { (_, response) ->
                        response.content().map { (_, mediaTypeObject) ->
                            mediaTypeObject.schema()
                        }
                    }.flatten()
                    parameterSchemas + requestBodySchemas + responseBodySchemas
                }.flatten()
            }.flatten()
            val dtoFiles = javaDtoWriter.write(dtoSchemas)
            outputFiles.addAll(dtoFiles)
            outputFiles.add(handlerAdapter(openApiSchema, converterRegistry))
            outputFiles.add(operations(openApiSchema, converterRegistry))
            outputFiles.add(configuration(openApiSchema))
        }

        return outputFiles
    }

    private fun handlerAdapter(openApiSchema: OpenApiSchema, converterRegistry: ConverterRegistry): OutputFile {
        val className = toJavaClassName(routesPackage, openApiSchema, "handler-adapter")
        val filePath = getFilePath(className)
        val pathEntries = openApiSchema.paths().pathItems().entries
        val pathPatternDeclarations = pathEntries.mapIndexed { index, (pathPattern, _) ->
            """private static final PathPattern pathPattern$index = PathPatternParser.defaultInstance.parse("$pathPattern");"""
        }
        val pathPatternMatchExpression = pathEntries.mapIndexed { index, (pathPattern, _) ->
            """pathPattern$index.matches(pathContainer)"""
        }.joinToString("\n    || ")
        val operationsClassName = toJavaClassName(routesPackage, openApiSchema, "operations")
        val matchCases = pathEntries.mapIndexed { index, (_, pathItem) ->
            val operationCases = pathItem.operations().map { operation ->
                val requestBody = operation.requestBody()
                val requestBodyType = bodyType(requestBody?.content())
                val checkMediaTypeBlock = if (requestBodyType != null) {
                    """|String contentType = request.getContentType();
                       |MediaType mediaType = MediaType.parseMediaType(contentType);
                       |if (!mediaType.equalsTypeAndSubtype(MediaType.parseMediaType("${requestBodyType.contentType}"))) {
                       |    throw new UnsupportedOperationException(contentType);
                       |}
                    """.trimMargin()
                } else {
                    ""
                }
                val extractBodyBlock = if (requestBodyType is BodyType.Json) {
                    val converterWriter = converterRegistry[requestBodyType.jsonSchema]
                    """|JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
                       |${converterWriter.valueType()} requestBodyDto = ${converterWriter.parseExpression("jsonNode")};
                    """.trimMargin()
                } else {
                    ""
                }
                val (responseCode, response) = response2xx(operation.responses())
                val responseBodyType = bodyType(response.content())

                val responseVariableDeclaration = if (responseBodyType is BodyType.Json) {
                    """${converterRegistry[responseBodyType.jsonSchema].valueType()} responseBody = """
                } else {
                    ""
                }
                val requestBodyArg = when (requestBodyType) {
                    null -> null
                    is BodyType.Binary -> "request.getInputStream()"
                    is BodyType.Json -> "requestBodyDto"
                }
                val operationArgs = (operation.parameters().mapIndexed { index, _ -> "param$index" }
                        + requestBodyArg
                        + if (responseBodyType is BodyType.Binary) "response.getOutputStream()" else null
                        ).filterNotNull().joinToString(",\n")
                val writeResponseBodyBlock = when (responseBodyType) {
                    is BodyType.Json -> {
                        val writeExpression = converterRegistry[responseBodyType.jsonSchema].writeExpression(
                            "jsonGenerator",
                            "responseBody"
                        )
                        """|JsonGenerator jsonGenerator = objectMapper.createGenerator(response.getOutputStream());
                           |List<? extends ValidationError> validationErrors = $writeExpression;
                           |jsonGenerator.close();
                           |if (!validationErrors.isEmpty()) {
                           |    throw new ValidationException(validationErrors);
                           |}
                        """.trimMargin()
                    }
                    else -> ""
                }
                val urlVariablesDefinition =
                    if (operation.parameters().any { parameter -> parameter.parameterIn == ParameterIn.PATH }) {
                        "Map<String, String> uriVariables = pathMatchInfo$index.getUriVariables();"
                    } else {
                        ""
                    }
                val parameterDefinitions = operation.parameters().mapIndexed { index, parameter ->
                    val schema = parameter.schema()
                    val parameterExpression = when (parameter.parameterIn) {
                        ParameterIn.PATH -> """uriVariables.get("${parameter.name}")"""
                        ParameterIn.QUERY -> """request.getParameter("${parameter.name}")"""
                        ParameterIn.HEADER -> """request.getHeader("${parameter.name}")"""
                    }
                    val converterWriter = converterRegistry[schema]
                    """|String param${index}Str = $parameterExpression;
                       |${converterWriter.valueType()} param$index = param${index}Str != null ? ${converterWriter.stringParseExpression("param${index}Str")} : null;
                    """.trimMargin()
                }
                val effectiveResponseCode = if (responseCode == "2xx") "200" else responseCode
                val setContentType = when (responseBodyType) {
                    null -> ""
                    is BodyType.Json -> """response.setContentType("application/json");"""
                    is BodyType.Binary -> """response.setContentType("${responseBodyType.contentType}");"""
                }
                """|if ("${operation.operationType.name}".equals(request.getMethod())) {
                   |    $urlVariablesDefinition
                   |    ${parameterDefinitions.indentWithMargin(1)}
                   |    ${checkMediaTypeBlock.indentWithMargin(1)}
                   |    ${extractBodyBlock.indentWithMargin(1)}
                   |    response.setStatus($effectiveResponseCode);
                   |    $setContentType
                   |    ${responseVariableDeclaration}operations.${toMethodName(operation.operationId)}(
                   |            ${operationArgs.indentWithMargin(3)}
                   |    );
                   |    ${writeResponseBodyBlock.indentWithMargin(1)}
                   |    return null;
                   |}
                """.trimMargin()
            }
            """|PathPattern.PathMatchInfo pathMatchInfo$index = pathPattern$index.matchAndExtract(pathContainer);
               |if (pathMatchInfo$index != null) {
               |    ${operationCases.indentWithMargin(1)}
               |}
            """.trimMargin()
        }
        val content =
            """|package ${getPackage(className)};
               |
               |import com.fasterxml.jackson.core.JsonGenerator;
               |import com.fasterxml.jackson.databind.JsonNode;
               |import com.fasterxml.jackson.databind.ObjectMapper;
               |import io.github.fomin.oasgen.MatchingHandlerAdapter;
               |import io.github.fomin.oasgen.ValidationError;
               |import io.github.fomin.oasgen.ValidationException;
               |import java.util.List;
               |import java.util.Map;
               |import javax.servlet.http.HttpServletRequest;
               |import javax.servlet.http.HttpServletResponse;
               |import org.springframework.http.MediaType;
               |import org.springframework.http.server.PathContainer;
               |import org.springframework.http.server.RequestPath;
               |import org.springframework.lang.NonNull;
               |import org.springframework.web.servlet.ModelAndView;
               |import org.springframework.web.util.pattern.PathPattern;
               |import org.springframework.web.util.pattern.PathPatternParser;
               |
               |public class ${getSimpleName(className)} implements MatchingHandlerAdapter {
               |    ${pathPatternDeclarations.indentWithMargin(1)}
               |
               |    private final String baseUrl;
               |    private final $operationsClassName operations;
               |    private final ObjectMapper objectMapper;
               |
               |    public ${getSimpleName(className)}(
               |            String baseUrl,
               |            $operationsClassName operations,
               |            ObjectMapper objectMapper
               |    ) {
               |        this.baseUrl = baseUrl;
               |        this.operations = operations;
               |        this.objectMapper = objectMapper;
               |    }
               |
               |    @Override
               |    public boolean supports(@NonNull Object handler) {
               |        return handler == this;
               |    }
               |
               |    @Override
               |    public ModelAndView handle(
               |            @NonNull HttpServletRequest request,
               |            @NonNull HttpServletResponse response,
               |            @NonNull Object handler
               |    ) throws Exception {
               |        PathContainer pathContainer = RequestPath.parse(request.getServletPath(), baseUrl).pathWithinApplication();
               |        ${matchCases.indentWithMargin(2)}
               |        response.setStatus(404);
               |        return null;
               |    }
               |
               |    @Override
               |    public long getLastModified(
               |            @NonNull HttpServletRequest request,
               |            @NonNull Object handler
               |    ) {
               |        return 0;
               |    }
               |
               |    @Override
               |    public boolean matches(HttpServletRequest request) {
               |        if (!request.getServletPath().startsWith(baseUrl)) {
               |            return false;
               |        }
               |        PathContainer pathContainer = RequestPath.parse(request.getServletPath(), baseUrl).pathWithinApplication();
               |        return ${pathPatternMatchExpression.indentWithMargin(3)};
               |    }
               |}
               |""".trimMargin()
        return OutputFile(filePath, content, OutputFileType.ROUTE)
    }

    private fun operations(openApiSchema: OpenApiSchema, converterRegistry: ConverterRegistry): OutputFile {
        val className = toJavaClassName(routesPackage, openApiSchema, "operations")
        val filePath = getFilePath(className)
        val methods = openApiSchema.paths().pathItems().entries.map { (_, pathItem) ->
            pathItem.operations().map { operation ->
                val (_, response) = response2xx(operation.responses())
                val responseBodyType = bodyType(response.content())
                val returnType = if (responseBodyType is BodyType.Json) {
                    converterRegistry[responseBodyType.jsonSchema].valueType()
                } else {
                    "void"
                }
                val parameterArgs = operation.parameters().map { parameter ->
                    val nullAnnotation = when {
                        parameter.required -> "@Nonnull"
                        else -> "@Nullable"
                    }
                    "$nullAnnotation ${converterRegistry[parameter.schema()].valueType()} ${toVariableName(parameter.name)}"
                }
                val requestBody = operation.requestBody()
                val requestBodyType = bodyType(requestBody?.content())
                val requestBodyArg = when (requestBodyType) {
                    null -> null
                    is BodyType.Json -> {
                        val valueType = converterRegistry[requestBodyType.jsonSchema].valueType()
                        """@Nonnull $valueType ${toVariableName(getSimpleName(valueType))}"""
                    }
                    is BodyType.Binary -> {
                        """@Nonnull java.io.InputStream inputStream"""
                    }
                }
                val responseBodyArg = if (responseBodyType is BodyType.Binary) {
                    """@Nonnull java.io.OutputStream outputStream"""
                } else {
                    null
                }
                val exceptions = if (requestBodyType is BodyType.Binary || responseBodyType is BodyType.Binary) {
                    " throws java.io.IOException"
                } else {
                    ""
                }

                val args = (parameterArgs + requestBodyArg + responseBodyArg).filterNotNull().joinToString(",\n")

                """|${returnType} ${toMethodName(operation.operationId)}(
                   |        ${args.indentWithMargin(2)}
                   |)$exceptions;
                   |
                """.trimMargin()
            }
        }.flatten()

        val content =
            """|package ${getPackage(className)};
               |
               |import javax.annotation.Nonnull;
               |import javax.annotation.Nullable;
               |
               |public interface ${getSimpleName(className)} {
               |    ${methods.indentWithMargin(1)}
               |}
               |
            """.trimMargin()
        return OutputFile(filePath, content, OutputFileType.ROUTE)
    }

    private fun configuration(openApiSchema: OpenApiSchema): OutputFile {
        val className = toJavaClassName(routesPackage, openApiSchema, "configuration")
        val operationsClassName = toJavaClassName(routesPackage, openApiSchema, "operations")
        val handlerAdapterClassName = toJavaClassName(routesPackage, openApiSchema, "handler-adapter")
        val filePath = getFilePath(className)
        val operationSimpleName = getSimpleName(operationsClassName)
        val (_, name) = TypeName.toTypeName(openApiSchema)
        val content =
            """|package ${getPackage(className)};
               |
               |import com.fasterxml.jackson.databind.ObjectMapper;
               |import io.github.fomin.oasgen.DefaultHandlerAdapterMapping;
               |import io.github.fomin.oasgen.MatchingHandlerAdapter;
               |import org.springframework.beans.factory.annotation.Value;
               |import org.springframework.context.annotation.Bean;
               |import org.springframework.context.annotation.Configuration;
               |import org.springframework.web.servlet.HandlerMapping;
               |
               |@Configuration
               |public class ${getSimpleName(className)} {
               |
               |    private final String basePath;
               |    private final $operationSimpleName ${toVariableName(operationSimpleName)};
               |    private final ObjectMapper objectMapper;
               |
               |    public ${getSimpleName(className)}(
               |            @Value("${'$'}{${operationsClassName}.basePath:}") String basePath,
               |            $operationSimpleName ${toVariableName(operationSimpleName)},
               |            ObjectMapper objectMapper
               |    ) {
               |        this.basePath = basePath;
               |        this.${toVariableName(operationSimpleName)} = ${toVariableName(operationSimpleName)};
               |        this.objectMapper = objectMapper;
               |    }
               |
               |    @Bean
               |    public HandlerMapping ${lowerFirst(name)}HandlerMapping() {
               |        return new DefaultHandlerAdapterMapping(${lowerFirst(name)}HandlerAdapter());
               |    }
               |
               |    @Bean
               |    public MatchingHandlerAdapter ${lowerFirst(name)}HandlerAdapter() {
               |        return new ${getSimpleName(handlerAdapterClassName)}(basePath, ${toVariableName(operationSimpleName)}, objectMapper);
               |    }
               |}
               |
            """.trimMargin()
        return OutputFile(filePath, content, OutputFileType.ROUTE)
    }
}
