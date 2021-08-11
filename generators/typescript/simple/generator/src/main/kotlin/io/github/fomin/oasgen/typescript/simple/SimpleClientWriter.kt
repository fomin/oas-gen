package io.github.fomin.oasgen.typescript.simple

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.generator.BodyType
import io.github.fomin.oasgen.generator.bodyType
import io.github.fomin.oasgen.generator.response2xx
import io.github.fomin.oasgen.java.*
import io.github.fomin.oasgen.typescript.dto.*

private class ClientFunction(
        val function: String,
        val dtoSchemas: List<JsonSchema>
)

class SimpleClientWriter(
        private val converterIds: List<String>
) : Writer<OpenApiSchema> {
    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {

        val typeConverterMatcher = TypeConverterMatcherProvider.provide(converterIds)
        val typeConverterRegistry = TypeConverterRegistry(typeConverterMatcher)

        return items.map { openApiSchema ->
            val clientFunctions = openApiSchema.paths().pathItems().flatMap { (pathTemplate, pathItem) ->
                pathItem.operations().map { operation ->
                    val requestBodyType = bodyType(operation.requestBody()?.content())
                    val (responseCode, response) = response2xx(operation.responses())
                    val responseBodyType = bodyType(response.content())

                    val returnType = when (responseBodyType) {
                        null -> "void"
                        is BodyType.Json -> typeConverterRegistry[responseBodyType.jsonSchema].type()
                        is BodyType.Binary -> "Blob"
                    }

                    val functionName = toLowerCamelCase(operation.operationId)
                    //TODO: Add processing of HEADER
                    val parameterArguments = operation.parameters().filter { parameter ->
                        parameter.parameterIn != ParameterIn.HEADER
                    }.map { parameter ->
                        val optionMark = if (parameter.required) "" else "?"
                        "${toLowerCamelCase(parameter.name)}$optionMark: ${typeConverterRegistry[parameter.schema()].type()}"
                    }
                    val bodyArgument = when (requestBodyType) {
                        null -> null
                        is BodyType.Json -> "body: ${typeConverterRegistry[requestBodyType.jsonSchema].type()}"
                        is BodyType.Binary -> "body: Blob"
                    }
                    val queryParameters = operation.parameters().filter { it.parameterIn == ParameterIn.QUERY }
                    val queryParameterDefinition = if (queryParameters.isNotEmpty()) {
                        val queryParameterItems = queryParameters.joinToString(",\n") { parameter ->
                            val jsonConverter = typeConverterRegistry[parameter.schema()].jsonConverter
                            val toStrExpression = jsonConverter?.toJson(parameter.name) ?: parameter.name
                            """(${parameter.name}) ? "${parameter.name}=" + encodeURIComponent($toStrExpression) : null"""
                        }
                        """|let queryParameters = [
                           |    ${queryParameterItems.indentWithMargin(1)}
                           |].filter(value => value != null).join("&")
                        """.trimMargin()
                    } else {
                        ""
                    }

                    val url = "${pathTemplateToUrl(pathTemplate)}${if (queryParameters.isNotEmpty()) "?${'$'}{queryParameters}" else ""}"
                    val responseTransformation = when (responseBodyType) {
                        null -> "value => undefined"
                        is BodyType.Json -> {
                            val jsonConverter = typeConverterRegistry[responseBodyType.jsonSchema].jsonConverter
                            "value => " + (jsonConverter?.fromJson("value") ?: "value")
                        }
                        is BodyType.Binary -> "value => value"
                    }

                    val responseType = when (responseBodyType) {
                        null -> """"text""""
                        is BodyType.Json -> """"json""""
                        is BodyType.Binary -> """"blob""""
                    }

                    val requestTransformation = when (requestBodyType) {
                        null -> "undefined"
                        is BodyType.Json -> {
                            val jsonConverter = typeConverterRegistry[requestBodyType.jsonSchema].jsonConverter
                            "JSON.stringify(${jsonConverter?.toJson("body") ?: "body"})"
                        }
                        is BodyType.Binary -> "body"
                    }

                    val methodArguments = listOf("baseUrl: string")
                        .plus(bodyArgument)
                        .plus(parameterArguments)
                        .plus(
                            listOf(
                                "timeout?: number",
                                "onLoadCallback?: (value: $returnType) => void",
                                "onErrorCallback?: (reason: any) => void",
                                "onTimeoutCallback?: () => void",
                                "onAbortCallback?: () => void"
                            )
                        )
                        .filterNotNull()
                    ClientFunction(
                            """|export function $functionName(
                               |    ${methodArguments.joinToString(",\n").indentWithMargin(1)}
                               |): RestRequest<$returnType> {
                               |    ${queryParameterDefinition.indentWithMargin(1)}
                               |    return new RestRequest<$returnType>(
                               |        `${'$'}{baseUrl}$url`,
                               |        "${operation.operationType.name}",
                               |        $responseTransformation,
                               |        $responseType,
                               |        $requestTransformation,
                               |        timeout,
                               |        onLoadCallback,
                               |        onErrorCallback,
                               |        onTimeoutCallback,
                               |        onAbortCallback
                               |    )
                               |}
                               |""".trimMargin(),
                        listOfNotNull(
                            requestBodyType?.jsonSchema(),
                            responseBodyType?.jsonSchema(),
                        ).plus(
                            operation
                                .parameters()
                                .filter { it.parameterIn != ParameterIn.HEADER }
                                .map { it.schema() }
                        )
                    )
                }
            }
            val schemaFileName = openApiSchema.fragment.reference.filePath.split("/").last()
            val schemaName = schemaFileName.substring(0, schemaFileName.lastIndexOf('.'))
            val outputFileName = "${toLowerCamelCase(schemaName)}.ts"

            val (dtoContent, dtoImportDeclarations) =
                    TypeScriptDtoWriter().write(typeConverterRegistry, clientFunctions.flatMap { it.dtoSchemas })

            val importDeclarations = dtoImportDeclarations + listOf(
                    ImportDeclaration("mapObjectProperties", "@andrey.n.fomin/oas-gen-typescript-dto-runtime"),
                    ImportDeclaration("RestRequest", "@andrey.n.fomin/oas-gen-typescript-dto-runtime")
            )

            val content =
                    """|${ImportDeclaration.toString(importDeclarations)}
                       |
                       |${clientFunctions.map { it.function }.indentWithMargin(0)}
                       |
                       |$dtoContent
                       |""".trimMargin()

            OutputFile(outputFileName, content, OutputFileType.ROUTE)
        }
    }

    private fun pathTemplateToUrl(pathTemplate: String): String {
        val sb = StringBuilder()
        var openBraceIndex = -1
        var closeBraceIndex = -1
        do {
            openBraceIndex = pathTemplate.indexOf('{', openBraceIndex + 1)
            if (openBraceIndex > 0) {
                sb.append(pathTemplate.substring(closeBraceIndex + 1, openBraceIndex))
                closeBraceIndex = pathTemplate.indexOf('}', closeBraceIndex + 1)
                if (closeBraceIndex > 0) {
                    val parameterName = pathTemplate.substring(openBraceIndex + 1, closeBraceIndex)
                    sb.append("${'$'}{${toLowerCamelCase(parameterName)}}")
                } else {
                    sb.append(pathTemplate.substring(openBraceIndex + 1))
                }
            } else {
                sb.append(pathTemplate.substring(closeBraceIndex + 1))
            }
        } while (openBraceIndex > 0)
        return sb.toString()
    }
}
