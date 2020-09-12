package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class Int64ConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema) =
            if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int64")
                object : Converter {
                    override val jsonSchema = jsonSchema
                    override fun valueType() = "java.lang.Long"
                    override fun extraAnnotations(): String? = null
                    override fun stringParseExpression(valueExpression: String) = "java.lang.Long.parseLong($valueExpression)"
                    override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
                    override fun output() = ConverterOutput.EMPTY
                }
            else null
}
