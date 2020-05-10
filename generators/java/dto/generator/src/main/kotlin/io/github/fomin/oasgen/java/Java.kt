package io.github.fomin.oasgen.java

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterRegistry

data class JavaVariable(
        val type: String,
        val name: String
)

data class JavaParameter(
        val name: String,
        val index: Int,
        val javaVariable: JavaVariable,
        val schemaParameter: Parameter
)

data class RequestVariable(
        val type: String,
        val parserType: String,
        val schema: JsonSchema,
        val contentType: String
)

data class ResponseVariable(
        val type: String?,
        val schema: JsonSchema?,
        val contentType: String
)

data class JavaOperation(
        val operation: Operation,
        val pathTemplate: String,
        val requestVariable: RequestVariable?,
        val responseVariable: ResponseVariable,
        val methodName: String,
        val parameters: List<JavaParameter>
)

fun toJavaOperations(converterRegistry: ConverterRegistry, paths: Paths): List<JavaOperation> {
    return paths.pathItems().flatMap { (pathTemplate, pathItem) ->
        pathItem.operations().map { operation ->
            val requestVariable = operation.requestBody()?.let {
                val mediaTypeObject = it.content()["application/json"]
                        ?: error("media type application/json is required")
                val requestSchema = mediaTypeObject.schema()
                val type = converterRegistry[requestSchema].valueType()
                RequestVariable(
                        type,
                        "$type.Parser",
                        requestSchema,
                        "application/json"
                )
            }

            val response = operation.responses().byCode()[HttpResponseCode.CODE_200]
                    ?: error("response 200 is required")
            val responseMediaTypeObject = response.content()["application/json"]
            val responseSchema = responseMediaTypeObject?.schema()
            val responseType = if (responseSchema != null)
                converterRegistry[responseSchema].valueType()
            else null
            val methodName = toMethodName(operation.operationId)
            val javaParameters = operation.parameters().mapIndexed { index, it ->
                val parameterType = converterRegistry[it.schema()].valueType()
                JavaParameter(it.name, index, JavaVariable(parameterType, toVariableName(it.name)), it)
            }

            JavaOperation(
                    operation,
                    pathTemplate,
                    requestVariable,
                    ResponseVariable(responseType, responseSchema, "application/json"),
                    methodName,
                    javaParameters
            )
        }
    }
}
