package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class NumberConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.NUMBER -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.math.BigDecimal"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.ScalarParser.createNumberParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.ScalarWriter.NUMBER_WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
            else -> null
        }
    }
}