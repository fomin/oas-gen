package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class BooleanConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.BOOLEAN) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.lang.Boolean"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.BooleanConverter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.BooleanConverter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}