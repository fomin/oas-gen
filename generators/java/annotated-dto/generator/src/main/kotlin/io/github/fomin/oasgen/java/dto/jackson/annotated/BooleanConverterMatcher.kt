package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class BooleanConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.BOOLEAN -> object : Converter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.lang.Boolean"
                override fun extraAnnotations(): String? = null
                override fun stringParseExpression(valueExpression: String) = "java.lang.Boolean.parseBoolean($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
                override fun output() = ConverterOutput.EMPTY
            }
            else -> null
        }
    }
}
