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

                override fun parseExpression(valueExpression: String, localVariableSuffix: Int): String {
                    val localValueName = "itemNode$localVariableSuffix"
                    val localParseExpression = converterRegistry[itemsSchema].parseExpression(
                        localValueName,
                        localVariableSuffix + 1
                    )
                    return "io.github.fomin.oasgen.ArrayConverter.parse($valueExpression, $localValueName -> $localParseExpression)"
                }

                override fun writeExpression(
                    jsonGeneratorName: String,
                    valueExpression: String,
                    localVariableSuffix: Int
                ): String {
                    val localJsonGeneratorName = "jsonGenerator$localVariableSuffix"
                    val localValueName = "item$localVariableSuffix"
                    val localWriteExpression = converterRegistry[itemsSchema].writeExpression(
                        localJsonGeneratorName,
                        localValueName,
                        localVariableSuffix + 1
                    )
                    return "io.github.fomin.oasgen.ArrayConverter.write($jsonGeneratorName, ($localJsonGeneratorName, $localValueName) -> $localWriteExpression, $valueExpression)"
                }

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
