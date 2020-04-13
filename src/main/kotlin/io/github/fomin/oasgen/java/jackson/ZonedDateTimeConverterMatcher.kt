package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class ZonedDateTimeConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date-time"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.ZonedDateTime"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.ZonedDateTimeConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.ZonedDateTimeConverter.WRITER"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
