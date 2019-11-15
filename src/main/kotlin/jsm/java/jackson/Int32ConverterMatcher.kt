package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class Int32ConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int32") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.lang.Integer"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.Int32Converter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.Int32Converter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}