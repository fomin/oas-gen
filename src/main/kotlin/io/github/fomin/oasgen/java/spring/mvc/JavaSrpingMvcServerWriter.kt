package io.github.fomin.oasgen.java.spring.mvc

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.jackson.*
import java.util.*

class JavaSrpingMvcServerWriter(
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
            val routesClassName = toJavaClassName(basePackage, openApiSchema, "routes")
            val filePath = getFilePath(routesClassName)

            val importDeclarations = TreeSet<String>()

            importDeclarations.addAll(listOf(
                    "import org.springframework.http.ResponseEntity;",
                    "import org.springframework.web.bind.annotation.*;"
            ))

            val paths = openApiSchema.paths()
            val javaOperations = toJavaOperations(converterRegistry, paths)

            val operationMethods = javaOperations.map { javaOperation ->
                val mappingAnnotationName = when (javaOperation.operation.operationType) {
                    OperationType.GET -> "GetMapping"
                    OperationType.POST -> "PostMapping"
                    OperationType.DELETE -> "DeleteMapping"
                }
                val consumesPart = when {
                    javaOperation.requestVariable != null -> """, consumes = "${javaOperation.responseVariable.contentType}""""
                    else -> ""
                }
                val requestBodyArg = javaOperation.requestVariable?.let { requestVariable ->
                    "@RequestBody ${requestVariable.type} ${toVariableName(getSimpleName(requestVariable.type))}"
                }
                val parameterArgs = javaOperation.parameters.map { javaParameter ->
                    val annotation = when (javaParameter.schemaParameter.parameterIn) {
                        ParameterIn.PATH -> "PathVariable"
                        ParameterIn.QUERY -> "RequestParam"
                    }
                    """@$annotation("${javaParameter.name}") ${javaParameter.javaVariable.type} ${javaParameter.javaVariable.name}"""
                }
                val methodArgs = (parameterArgs + requestBodyArg).filterNotNull().joinToString(",\n")
                val responseType = javaOperation.responseVariable.type ?: "java.lang.Void"
                """|@$mappingAnnotationName(path = "${javaOperation.pathTemplate}", produces = "${javaOperation.responseVariable.contentType}"$consumesPart)
                   |ResponseEntity<$responseType> ${javaOperation.methodName}(
                   |        ${methodArgs.indentWithMargin(2)}
                   |);
                   |
                """.trimMargin()
            }

            val content = """
               |package ${getPackage(routesClassName)};
               |
               |${importDeclarations.indentWithMargin(0)}
               |
               |public interface ${getSimpleName(routesClassName)} {
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

            val configurationWriter = ConfigurationWriter()
            val configuraionClassSimpleName = getSimpleName(toJavaClassName(basePackage, openApiSchema, "web-mvc-configuration"))
            val configurationOutputFile = configurationWriter.write(basePackage, configuraionClassSimpleName, messageConverterClassSimpleName)
            outputFiles.add(configurationOutputFile)

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