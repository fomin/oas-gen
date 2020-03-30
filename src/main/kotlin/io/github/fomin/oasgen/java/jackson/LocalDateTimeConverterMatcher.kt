package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateTimeConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "local-date-time")
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.time.LocalDateTime"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.ScalarParser.createStringLocalDateTimeParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.ScalarWriter.STRING_LOCAL_DATE_TIME_WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}