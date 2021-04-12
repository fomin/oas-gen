package io.github.fomin.oasgen.java.dto.jackson.wstatic

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
                override fun parseExpression(valueExpression: String) =
                    "io.github.fomin.oasgen.NumberConverter.parse($valueExpression)"
                override fun writeExpression(valueExpression: String) =
                    "io.github.fomin.oasgen.NumberConverter.write(jsonGenerator, $valueExpression)"
                override fun stringParseExpression(valueExpression: String) = "new java.math.BigDecimal($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toPlainString()"
                override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
            }
            else -> null
        }
    }
}