package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class ArrayConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "array"
        override fun provide(dtoPackage: String, routesPackage: String) = ArrayConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.ARRAY -> object : ConverterWriter {
                private val itemsSchema = jsonSchema.items()
                        ?: error("array schema should have items schema at $jsonSchema")

                override val jsonSchema = jsonSchema

                override fun valueType() =
                        "java.util.List<${converterRegistry[itemsSchema].valueType()}>"

                override fun parseExpression(valueExpression: String) =
                        "io.github.fomin.oasgen.ArrayConverter.parse($valueExpression, itemNode -> ${converterRegistry[itemsSchema].parseExpression("itemNode")})"

                override fun writeExpression(jsonGeneratorName: String, valueExpression: String) =
                        "io.github.fomin.oasgen.ArrayConverter.write($jsonGeneratorName, (${jsonGeneratorName}1, item) -> ${converterRegistry[itemsSchema].writeExpression("${jsonGeneratorName}1", "item")}, $valueExpression)"

                override fun stringParseExpression(valueExpression: String) = throw UnsupportedOperationException()

                override fun stringWriteExpression(valueExpression: String) = throw UnsupportedOperationException()

                override fun generate(): ConverterWriter.Result {
                    return ConverterWriter.Result(emptyList(), listOf(itemsSchema))
                }
            }
            else -> null
        }
    }
}
