package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date" || jsonSchema.format == "local-date"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.time.LocalDate"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.LocalDateConverter.createParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.LocalDateConverter.WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
