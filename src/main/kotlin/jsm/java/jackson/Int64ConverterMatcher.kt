package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class Int64ConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int64") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.lang.Long"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.Int64Converter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.Int64Converter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}