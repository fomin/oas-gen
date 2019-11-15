package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class LocalDateTimeConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "local-date-time")
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.time.LocalDateTime"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.ScalarParser.createStringLocalDateTimeParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.ScalarWriter.STRING_LOCAL_DATE_TIME_WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}