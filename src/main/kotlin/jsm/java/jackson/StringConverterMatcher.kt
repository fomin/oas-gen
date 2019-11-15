package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class StringConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.STRING -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.lang.String"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.ScalarParser.createStringParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.ScalarWriter.STRING_WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
            else -> null
        }
    }
}