package io.github.fomin.oasgen.typescript.simple

import io.github.fomin.oasgen.*
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
                    val response200 = operation.responses().singleOrNull2xx()?.value
                    val responseEntry = response200?.let { response ->
                        val entries = response.content().entries
                        if (entries.isEmpty()) {
                            null
                        } else {
                             entries.single()
                        }
                    }
                    val responseSchema = responseEntry?.value?.schema()
                    val returnType = if (responseSchema == null) "void"
                    else typeConverterRegistry[responseSchema].type()

                    val functionName = toLowerCamelCase(operation.operationId)
                    //TODO: Add processing of HEADER
                    val parameterArguments = operation.parameters().filter { parameter ->
                        parameter.parameterIn != ParameterIn.HEADER
                    }.map { parameter ->
                        val optionMark = if (parameter.required) "" else "?"
                        "${toLowerCamelCase(parameter.name)}$optionMark: ${typeConverterRegistry[parameter.schema()].type()}"
                    }
                    val requestEntry = operation.requestBody()?.content()?.entries?.single()
                    val bodySchema = requestEntry?.value?.schema()
                    val bodyArguments = if (bodySchema == null) emptyList()
                    else listOf("body: ${typeConverterRegistry[bodySchema].type()}")
                    val parameterStrDefinitions = operation.parameters().mapNotNull { parameter ->
                        if (parameter.parameterIn == ParameterIn.QUERY) {
                            val jsonConverter = typeConverterRegistry[parameter.schema()].jsonConverter
                            val toStrExpression = jsonConverter?.toJson(parameter.name) ?: parameter.name
                            """|let ${parameter.name}Str
                               |if (${parameter.name}) {
                               |    ${parameter.name}Str = encodeURIComponent($toStrExpression)
                               |} else {
                               |    ${parameter.name}Str = ""
                               |}
                            """.trimMargin()
                        } else {
                            null
                        }
                    }
                    val queryString = operation.parameters()
                            .filter { parameter -> parameter.parameterIn != ParameterIn.HEADER }
                            .mapNotNull { parameter ->
                        when (parameter.parameterIn) {
                            ParameterIn.QUERY -> "${parameter.name}=${'$'}{${parameter.name}Str}"
                            else -> null
                        }
                    }.joinToString("&")
                    val url = "${pathTemplateToUrl(pathTemplate)}${if (queryString.isNotEmpty()) "?$queryString" else ""}"
                    val responseTransformation = if (responseSchema != null) {
                        val jsonConverter = typeConverterRegistry[responseSchema].jsonConverter
                        "value => " + (jsonConverter?.fromJson("value") ?: "value")
                    } else {
                        "value => undefined"
                    }

                    val responseType = when {
                        responseEntry != null -> when (responseEntry.key) {
                            "application/json" -> """"json""""
                            else -> """"text""""
                        }
                        else -> """"text""""
                    }

                    val requestTransformation = if (bodySchema != null) {
                        val jsonConverter = typeConverterRegistry[bodySchema].jsonConverter
                        "JSON.stringify(${jsonConverter?.toJson("body") ?: "body"})"
                    } else {
                        "undefined"
                    }

                    val methodArguments = listOf(
                            "baseUrl: string"
                    ) + bodyArguments + parameterArguments + listOf(
                            "timeout?: number",
                            "onLoadCallback?: (value: $returnType) => void",
                            "onErrorCallback?: (reason: any) => void",
                            "onTimeoutCallback?: () => void",
                            "onAbortCallback?: () => void"
                    )
                    ClientFunction(
                            """|export function $functionName(
                               |    ${methodArguments.joinToString(",\n").indentWithMargin(1)}
                               |): RestRequest<$returnType> {
                               |    ${parameterStrDefinitions.indentWithMargin(1)}
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
                            listOfNotNull(bodySchema, responseSchema) + operation.parameters().filter { it.parameterIn != ParameterIn.HEADER }.map { it.schema() }
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
