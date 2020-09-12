package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class MapConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        val additionalProperties = jsonSchema.additionalProperties()
        return if (jsonSchema.type is JsonType.OBJECT && additionalProperties != null) object : Converter {
            override val jsonSchema = jsonSchema

            override fun valueType() =
                    "java.util.Map<java.lang.String, ${converterRegistry[additionalProperties].valueType()}>"

            override fun extraAnnotations(): String? = null

            override fun stringParseExpression(valueExpression: String) = throw UnsupportedOperationException()

            override fun stringWriteExpression(valueExpression: String) = throw UnsupportedOperationException()

            override fun output() = ConverterOutput.EMPTY
        }
        else null
    }
}
