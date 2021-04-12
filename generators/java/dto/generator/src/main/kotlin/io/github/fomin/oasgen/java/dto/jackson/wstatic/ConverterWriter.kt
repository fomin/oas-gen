package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.OutputFile

interface ConverterWriter {
    data class Result(val outputFiles: List<OutputFile>, val jsonSchemas: List<JsonSchema>)

    val jsonSchema: JsonSchema
    fun valueType(): String
    fun parseExpression(valueExpression: String): String
    fun writeExpression(valueExpression: String): String
    fun stringParseExpression(valueExpression: String): String
    fun stringWriteExpression(valueExpression: String): String
    fun generate(): Result
}
