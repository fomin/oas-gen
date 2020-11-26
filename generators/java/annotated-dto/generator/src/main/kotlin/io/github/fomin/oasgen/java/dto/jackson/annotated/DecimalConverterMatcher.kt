package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class DecimalConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "decimal") object : Converter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.math.BigDecimal"
            override fun extraAnnotations(): String? = null
            override fun stringParseExpression(valueExpression: String) = "new java.math.BigDecimal($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toPlainString()"
            override fun output() = ConverterOutput.EMPTY
        }
        else null
    }
}
