package io.github.fomin.oasgen.java.destruction.test

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.generator.bodyType
import io.github.fomin.oasgen.generator.response2xx
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterMatcherProvider
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterRegistry
import io.github.fomin.oasgen.java.dto.jackson.wstatic.JavaDtoWriter

class JavaDestructionTestWriter(
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
//            val dtoSchemas = openApiSchema.paths().pathItems().map { (_, pathItem) ->
//                pathItem.operations().map { operation ->
//                    val parameterSchemas = operation.parameters().map { parameter ->
//                        parameter.schema()
//                    }
//                    val requestBodySchemas = when (val requestBody = operation.requestBody()) {
//                        null -> emptyList()
//                        else -> requestBody.content().map { (_, mediaTypeObject) ->
//                            mediaTypeObject.schema()
//                        }
//                    }
//                    val responseBodySchemas = operation.responses().byCode().map { (_, response) ->
//                        response.content().map { (_, mediaTypeObject) ->
//                            mediaTypeObject.schema()
//                        }
//                    }.flatten()
//                    parameterSchemas + requestBodySchemas + responseBodySchemas
//                }.flatten()
//            }.flatten()
            outputFiles.addAll(writeDataProviders(openApiSchema, converterRegistry))
            outputFiles.add(writeAbstractTest(openApiSchema, converterRegistry))
        }

        return outputFiles
    }

    private fun getDataProviderClassName(operation: Operation): String =
        toJavaClassName(routesPackage, operation, "provider")

    private fun writeDataProviders(openApiSchema: OpenApiSchema, converterRegistry: ConverterRegistry) =
        openApiSchema.paths()
            .pathItems()
            .entries
            .flatMap { entry ->
                entry.value.operations()
                    .map {
                        OutputFile(
                            getFilePath(getDataProviderClassName(it)),
                            writeDataProvider(entry.key, it, converterRegistry),
                            OutputFileType.ROUTE
                        )
                    }
            }
            .toList()

    private fun writeDataProvider(path: String, operation: Operation, converterRegistry: ConverterRegistry): String {
        val body = bodyType(operation.requestBody()?.content())?.jsonSchema()?.let {  "new Parameter<${converterRegistry[it].valueType()}>(${converterRegistry[it].valueType()}.class, null, (jsonGenerator, p) -> ${converterRegistry[it].writeExpression("jsonGenerator","p")})" } ?: "null"
        val content =
            """ |${getRequestHeaders(operation, converterRegistry).map { "headers.put(\"${it.key}\", ${it.value});" }.indentWithMargin(0)}
                |${getRequestPathParams(operation, converterRegistry).map { "pathParams.put(\"${it.key}\", ${it.value});" }.indentWithMargin(0)}
                |${getRequestQueryParams(operation, converterRegistry).map { "queryParams.put(\"${it.key}\", ${it.value});" }.indentWithMargin(0)}
                |Request request = new OriginRequest("${operation.operationId}", "$path", "${operation.operationType}", headers, null, pathParams, queryParams, $body);
                |""".trimMargin().removeBlankLines()

        return """ |package ${getPackage(getDataProviderClassName(operation))};
                   |
                   |import io.github.fomin.oasgen.MutatedRequestInvocationContext;
                   |import io.github.fomin.oasgen.OriginRequest;
                   |import io.github.fomin.oasgen.Parameter;
                   |import io.github.fomin.oasgen.Request;
                   |import io.github.fomin.oasgen.RequestMutator;
                   |import io.github.fomin.oasgen.RequestMutatorImpl;
                   |import org.junit.jupiter.api.extension.ExtensionContext;
                   |import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
                   |import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
                   |
                   |import java.util.HashMap;
                   |import java.util.Map;
                   |import java.util.stream.Stream;
                   |
                   |public class ${getSimpleName(getDataProviderClassName(operation))} implements TestTemplateInvocationContextProvider {
                   |    private final RequestMutator mutator = new RequestMutatorImpl();
                   |
                   |    @Override
                   |    public boolean supportsTestTemplate(ExtensionContext context) {
                   |        return true;
                   |    }
                   |
                   |    @Override
                   |    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
                   |        Map<String, Parameter<?>> headers = new HashMap<>();
                   |        Map<String, Parameter<?>> pathParams = new HashMap<>();
                   |        Map<String, Parameter<?>> queryParams = new HashMap<>();
                   |        ${content.indentWithMargin(2)}
                   |        return mutator.mutate(request).map(MutatedRequestInvocationContext::new);
                   |    }
                   |}
                   |""".trimMargin()
    }

    private fun getRequestHeaders(operation: Operation, converterRegistry: ConverterRegistry) : Map<String, String> {
        val (_, response) = response2xx(operation.responses())
        val headers = HashMap<String, String>()
        val responseBodyType = bodyType(response.content())
        responseBodyType?.contentType?.let {
            headers["Accept"] = "new Parameter<String>(String.class, \"$it\", java.util.function.Function.identity())"
        }
        bodyType(operation.requestBody()?.content())?.let {
            headers["Content-Type"] = "new Parameter<String>(String.class, \"${it.contentType}\", java.util.function.Function.identity())"
        }

        operation.parameters()
            .filter { it.parameterIn == ParameterIn.HEADER }
            .forEach {
                headers[it.name] = "new Parameter<${converterRegistry[it.schema()].valueType()}>(${converterRegistry[it.schema()].valueType()}.class, null, p -> ${converterRegistry[it.schema()].stringWriteExpression("p")})"
            }
        return headers
    }

    private fun getRequestPathParams(operation: Operation, converterRegistry: ConverterRegistry) : Map<String, String> {
        val params = HashMap<String, String>()
        operation.parameters()
            .filter { it.parameterIn == ParameterIn.PATH }
            .forEach {
                params[it.name] = "new Parameter<${converterRegistry[it.schema()].valueType()}>(${converterRegistry[it.schema()].valueType()}.class, null, p -> ${converterRegistry[it.schema()].stringWriteExpression("p")})"
            }
        return params
    }

    private fun getRequestQueryParams(operation: Operation, converterRegistry: ConverterRegistry) : Map<String, String> {
        val params = HashMap<String, String>()
        operation.parameters()
            .filter { it.parameterIn == ParameterIn.QUERY }
            .forEach {
                params[it.name] = "new Parameter<${converterRegistry[it.schema()].valueType()}>(${converterRegistry[it.schema()].valueType()}.class, null, p -> ${converterRegistry[it.schema()].stringWriteExpression("p")})"
            }
        return params
    }

    private fun writeAbstractTest(openApiSchema: OpenApiSchema, converterRegistry: ConverterRegistry): OutputFile {
        val className = toJavaClassName(routesPackage, openApiSchema, prefix = "abstract", suffix = "destruction-test")
        val filePath = getFilePath(className)
        val pathEntries = openApiSchema.paths().pathItems().entries
        val testMethods = pathEntries.mapIndexed { _, (_, pathItem) ->
            val operationCases = pathItem.operations().map { operation ->
                """|
                    |@TestTemplate
                    |@ExtendWith(${toJavaClassName(routesPackage, operation, "provider")}.class)
                    |public final void ${toMethodName(operation.operationId)}Test(Request request) {
                    |    getExecutor().execute(enrichRequest(request));
                    |    healthCheck();
                    |}
                    |""".trimMargin()

            }
            operationCases.indentWithMargin(1)
        }

        val content =
            """ |package ${getPackage(className)};
                |
                |import io.github.fomin.oasgen.DestructionTest;
                |import io.github.fomin.oasgen.Request;
                |import io.github.fomin.oasgen.RequestExecutor;
                |import org.junit.jupiter.api.TestTemplate;
                |import org.junit.jupiter.api.extension.ExtendWith;
                |
                |public abstract class ${getSimpleName(className)} implements DestructionTest {
                |
                |    public abstract RequestExecutor getExecutor();
                |
                |    public abstract void healthCheck();
                |
                |    public Request enrichRequest(Request request) {
                |        return request;
                |    }
                |
                ${testMethods.indentWithMargin(1).trimMargin()}
                |}
                |""".trimMargin()
        return OutputFile(filePath, content, OutputFileType.ROUTE)
    }
}
