package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class DoubleConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return if (jsonSchema.type is JsonType.Scalar.NUMBER && jsonSchema.format == "double") object : Converter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Double"
            override fun extraAnnotations(): String? = null
            override fun stringParseExpression(valueExpression: String) = "java.lang.Double.parseDouble($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun output() = ConverterOutput.EMPTY
        }
        else null
    }
}
