package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class OffsetDateTimeConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "date-time"
        override fun provide(basePackage: String) = OffsetDateTimeConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date-time"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.OffsetDateTime"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.OffsetDateTimeConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.OffsetDateTimeConverter.WRITER"
                override fun stringParseExpression(valueExpression: String) = "java.time.OffsetDateTime.parse($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
