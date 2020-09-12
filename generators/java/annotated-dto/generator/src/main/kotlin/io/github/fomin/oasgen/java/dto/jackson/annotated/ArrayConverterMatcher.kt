package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class ArrayConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return when (jsonSchema.type) {
            is JsonType.ARRAY -> object : Converter {
                private val itemsSchema = jsonSchema.items()
                        ?: error("array schema should have items schema at $jsonSchema")

                override val jsonSchema = jsonSchema

                override fun valueType() =
                        "java.util.List<${converterRegistry[itemsSchema].valueType()}>"

                override fun extraAnnotations(): String? = null

                override fun stringParseExpression(valueExpression: String) = throw UnsupportedOperationException()

                override fun stringWriteExpression(valueExpression: String) = throw UnsupportedOperationException()

                override fun output() = ConverterOutput(null, listOf(itemsSchema))
            }
            else -> null
        }
    }
}
