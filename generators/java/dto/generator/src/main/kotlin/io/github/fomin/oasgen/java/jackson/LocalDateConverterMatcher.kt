package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date" || jsonSchema.format == "local-date"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDate"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.LocalDateConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.LocalDateConverter.WRITER"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
