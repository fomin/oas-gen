package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class NumberConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "number"
        override fun provide(basePackage: String) = NumberConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.NUMBER -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.math.BigDecimal"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.ScalarParser.createNumberParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.ScalarWriter.NUMBER_WRITER"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
            else -> null
        }
    }
}