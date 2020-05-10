package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.OutputFile

interface ConverterWriter {
    data class Result(val outputFile: OutputFile?, val jsonSchemas: List<JsonSchema>)

    val jsonSchema: JsonSchema
    fun valueType(): String
    fun parserCreateExpression(): String
    fun writerCreateExpression(): String
    fun generate(): Result
}
