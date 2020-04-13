package io.github.fomin.oasgen.java.rest.operations

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.jackson.*
import io.github.fomin.oasgen.java.spring.mvc.MessageConverterWriter
import java.util.*

class JavaSpringRestOperationsWriter(
        private val basePackage: String
) : Writer<OpenApiSchema> {
    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {
        val outputFiles = mutableListOf<OutputFile>()

        val converterRegistry = ConverterRegistry(listOf(
                OffsetDateTimeConverterMatcher(),
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
            val clientClassName = toJavaClassName(basePackage, openApiSchema, "client")
            val filePath = getFilePath(clientClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import java.net.URI;",
                    "import java.util.Collections;",
                    "import java.util.HashMap;",
                    "import java.util.Map;",
                    "import org.springframework.http.MediaType;",
                    "import org.springframework.http.RequestEntity;",
                    "import org.springframework.http.ResponseEntity;",
                    "import org.springframework.web.client.RestOperations;",
                    "import org.springframework.web.util.UriComponentsBuilder;"
            ))

            val paths = openApiSchema.paths()
            val javaOperations = toJavaOperations(converterRegistry, paths)

            val operationMethods = javaOperations.map { javaOperation ->
                val requestVariable = javaOperation.requestVariable
                val requestBodyArgDeclaration = requestVariable?.let {
                    "${it.type} ${toVariableName(getSimpleName(it.type))}"
                }
                val requestBodyInternalArgDeclaration = requestVariable?.let {
                    "${it.type} bodyArg"
                }
                val requestBodyArg = requestVariable?.let {
                    toVariableName(getSimpleName(it.type))
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

                val pathParameters = javaOperation.parameters.filter { it.schemaParameter.parameterIn == ParameterIn.PATH }

                val uriVariablesBlock = if (pathParameters.isEmpty()) {
                    "Map<String, Object> uriVariables = Collections.emptyMap();"
                } else {
                    """|Map<String, Object> uriVariables = new HashMap<>();
                       |${pathParameters.map { "uriVariables.put(\"${it.name}\", param${it.index});" }.indentWithMargin(0)}
                    """.trimMargin()
                }

                val (requestType, buildRequestExpression) = when {
                    requestVariable != null -> Pair(
                            requestVariable.type,
                            """|.contentType(MediaType.APPLICATION_JSON)
                               |.body(bodyArg, ${requestVariable.type}.class);
                               |""".trimMargin()
                    )
                    else -> Pair(
                            "java.lang.Void",
                            ".build();"
                    )
                }

                val queryParameterCalls = javaOperation.parameters.mapNotNull { javaParameter ->
                    if (javaParameter.schemaParameter.parameterIn == ParameterIn.QUERY)
                        """.queryParam("${javaParameter.schemaParameter.name}", param${javaParameter.index})"""
                    else
                        null
                }

                """|public ResponseEntity<$responseType> ${javaOperation.methodName}(
                   |        ${methodArgDeclarations.indentWithMargin(2)}
                   |) {
                   |    return ${javaOperation.methodName}$0(
                   |            ${methodArgs.indentWithMargin(2)}
                   |    );
                   |}
                   |
                   |private ResponseEntity<$responseType> ${javaOperation.methodName}$0(
                   |        ${methodInternalArgDeclarations.indentWithMargin(2)}
                   |) {
                   |    ${uriVariablesBlock.indentWithMargin(1)}
                   |    URI uri = UriComponentsBuilder
                   |            .fromUriString(baseUrl + "${javaOperation.pathTemplate}")
                   |            ${queryParameterCalls.indentWithMargin(3)}
                   |            .build(uriVariables);
                   |    RequestEntity<$requestType> request = RequestEntity
                   |            .${javaOperation.operation.operationType.name.toLowerCase()}(uri)
                   |            ${buildRequestExpression.indentWithMargin(3)}
                   |    return restOperations.exchange(request, $responseType.class);
                   |}
                   |""".trimMargin()
            }

            val content = """
               |package ${getPackage(clientClassName)};
               |
               |${importDeclarations.indentWithMargin(0)}
               |
               |public class ${getSimpleName(clientClassName)} {
               |    private final RestOperations restOperations;
               |    private final String baseUrl;
               |
               |    public ${getSimpleName(clientClassName)}(RestOperations restOperations, String baseUrl) {
               |        this.restOperations = restOperations;
               |        this.baseUrl = baseUrl;
               |    }
               |
               |    ${operationMethods.indentWithMargin(1)}
               |
               |}
               |
            """.trimMargin()

            val messageConverterWriter = MessageConverterWriter()
            val messageConverterClassSimpleName = getSimpleName(toJavaClassName(basePackage, openApiSchema, "message-converter"))
            val messageConverterOutputFile = messageConverterWriter.write(basePackage, messageConverterClassSimpleName, converterRegistry, javaOperations)
            outputFiles.add(messageConverterOutputFile)

            val dtoSchemas = mutableListOf<JsonSchema>()
            dtoSchemas.addAll(javaOperations.mapNotNull { it.responseVariable.schema })
            dtoSchemas.addAll(javaOperations.mapNotNull { it.requestVariable?.schema })
            val dtoFiles = javaDtoWriter.write(dtoSchemas)
            outputFiles.addAll(dtoFiles)
            outputFiles.add(OutputFile(filePath, content))
        }

        return outputFiles
    }

}
