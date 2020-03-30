package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class NumberConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.NUMBER -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.math.BigDecimal"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.ScalarParser.createNumberParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.ScalarWriter.NUMBER_WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
            else -> null
        }
    }
}