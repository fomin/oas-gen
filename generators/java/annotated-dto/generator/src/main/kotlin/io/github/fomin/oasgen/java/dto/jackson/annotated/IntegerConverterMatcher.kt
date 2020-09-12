package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class IntegerConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.INTEGER -> object : Converter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.math.BigInteger"
                override fun extraAnnotations(): String? = null
                override fun stringParseExpression(valueExpression: String) = "new java.math.BigInteger($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
                override fun output() = ConverterOutput.EMPTY
            }
            else -> null
        }
    }
}
