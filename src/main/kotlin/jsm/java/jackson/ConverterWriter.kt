package jsm.java.jackson

import jsm.JsonSchema
import jsm.OutputFile

interface ConverterWriter {
    data class Result(val outputFile: OutputFile?, val jsonSchemas: List<JsonSchema>)

    val jsonSchema: JsonSchema
    fun valueType(converterRegistry: ConverterRegistry): String
    fun parserCreateExpression(converterRegistry: ConverterRegistry): String
    fun writerCreateExpression(converterRegistry: ConverterRegistry): String
    fun generate(converterRegistry: ConverterRegistry): Result
}
