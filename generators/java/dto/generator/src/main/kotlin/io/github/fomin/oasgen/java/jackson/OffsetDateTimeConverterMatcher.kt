package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class OffsetDateTimeConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date-time"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.OffsetDateTime"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.OffsetDateTimeConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.OffsetDateTimeConverter.WRITER"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
