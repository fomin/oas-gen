package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class NumberConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.NUMBER -> object : Converter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.math.BigDecimal"
                override fun extraAnnotations(): String? = null
                override fun output() = ConverterOutput.EMPTY
            }
            else -> null
        }
    }
}
