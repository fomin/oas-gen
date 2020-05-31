package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateTimeConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "local-date-time"
        override fun provide(basePackage: String) = LocalDateTimeConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "local-date-time")
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDateTime"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.LocalDateTimeConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.LocalDateTimeConverter.WRITER"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}