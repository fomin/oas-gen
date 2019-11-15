package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class IntegerConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.math.BigInteger"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.IntegerConverter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.IntegerConverter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}