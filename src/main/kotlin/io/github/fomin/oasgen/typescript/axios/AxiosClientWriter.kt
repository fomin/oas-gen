package io.github.fomin.oasgen.typescript.axios

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.toLowerCamelCase
import io.github.fomin.oasgen.typescript.*
import java.lang.StringBuilder

private fun axiosMethod(operationType: OperationType) = when (operationType) {
    OperationType.POST -> "post"
    OperationType.DELETE -> "delete"
    OperationType.GET -> "get"
}

private class ClientFunction(
        val function: String,
        val dtoSchemas: List<JsonSchema>
)

class AxiosClientWriter(val basePackage: String) : Writer<OpenApiSchema> {
    override fun write(items: Iterable<OpenApiSchema>): List<OutputFile> {

        val typeConverterRegistry = TypeConverterRegistry(listOf(
                LocalDateTimeConverterMatcher(),
                StringEnumConverterMatcher(),
                MapConverterMatcher(),
                ArrayConverterMatcher(),
                BooleanConverterMatcher(),
                NumberConverterMatcher(),
                IntegerConverterMatcher(),
                StringConverterMatcher(),
                ObjectConverterMatcher()
        ))

        return items.map { openApiSchema ->
            val clientFunctions = openApiSchema.paths().pathItems().flatMap { (pathTemplate, pathItem) ->
                pathItem.operations().map { operation ->
                    val response200 = operation.responses().byCode()[HttpResponseCode.CODE_200]
                    val responseSchema = response200?.let { response ->
                        val entries = response.content().entries
                        if (entries.isEmpty()) {
                            null
                        } else {
                            val (_, mediaTypeObject) = entries.single()
                            mediaTypeObject.schema()
                        }
                    }
                    val returnType = if (responseSchema == null) "void"
                    else typeConverterRegistry[responseSchema].type()

                    val functionName = toLowerCamelCase(operation.operationId)
                    val parameterArguments = operation.parameters().map { parameter ->
                        ", ${toLowerCamelCase(parameter.name)}: ${typeConverterRegistry[parameter.schema()].type()}"
                    }
                    val bodySchema = operation.requestBody()?.let { requestBody ->
                        val (_, mediaTypeObject) = requestBody.content().entries.single()
                        mediaTypeObject.schema()
                    }
                    val bodyArguments = if (bodySchema == null) emptyList()
                    else listOf(", body: ${typeConverterRegistry[bodySchema].type()}")
                    val allArguments = (parameterArguments + bodyArguments).joinToString("")

                    val axiosMethod = axiosMethod(operation.operationType)
                    val axiosBodyArg = if (bodySchema == null) ""
                    else ", ${typeConverterRegistry[bodySchema].jsonConverter?.toJson("body")}"
                    val url = pathTemplateToUrl(pathTemplate)
                    val responseTransformation = if (responseSchema != null) {
                        val jsonConverter = typeConverterRegistry[responseSchema].jsonConverter
                        if (jsonConverter != null) "value => ${jsonConverter.fromJson("value.data")}"
                        else "value => value.data"
                    } else {
                        "value => null"
                    }

                    ClientFunction(
                            """|export function $functionName(axios: AxiosInstance$allArguments): Promise<$returnType> {
                               |    return axios.$axiosMethod(`$url`$axiosBodyArg).then($responseTransformation);
                               |}
                               |""".trimMargin(),
                            listOfNotNull(bodySchema, responseSchema)
                    )
                }
            }
            val schemaFileName = openApiSchema.fragment.reference.filePath.split("/").last()
            val schemaName = schemaFileName.substring(0, schemaFileName.lastIndexOf('.'))
            val outputFileName = "${toLowerCamelCase(schemaName)}.ts"

            val (dtoContent, dtoImportDeclarations) =
                    TypeScriptDtoWriter().write(typeConverterRegistry, clientFunctions.flatMap { it.dtoSchemas })

            val importDeclarations = dtoImportDeclarations + listOf(
                    ImportDeclaration("AxiosInstance", "axios")
            )

            val content =
                    """|${ImportDeclaration.toString(importDeclarations)}
                       |
                       |${clientFunctions.map { it.function }.indentWithMargin(0)}
                       |
                       |$dtoContent
                       |""".trimMargin()

            OutputFile(outputFileName, content)
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
