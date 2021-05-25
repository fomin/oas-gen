package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class MapConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "map"
        override fun provide(dtoPackage: String, routesPackage: String) = MapConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        val additionalProperties = jsonSchema.additionalProperties()
        return if (jsonSchema.type is JsonType.OBJECT && additionalProperties != null) object : ConverterWriter {
            override val jsonSchema = jsonSchema

            override fun valueType() =
                    "java.util.Map<java.lang.String, ${converterRegistry[additionalProperties].valueType()}>"

            override fun parseExpression(valueExpression: String) =
                    "new io.github.fomin.oasgen.MapConverter.parse($valueExpression, itemNode -> ${converterRegistry[additionalProperties].parseExpression("itemNode")})"

            override fun writeExpression(jsonGeneratorName: String, valueExpression: String) =
                    "new io.github.fomin.oasgen.MapConverter<>($jsonGeneratorName, (${jsonGeneratorName}1, value) -> ${converterRegistry[additionalProperties].writeExpression("${jsonGeneratorName}1", "value")})"

            override fun stringParseExpression(valueExpression: String) = throw UnsupportedOperationException()

            override fun stringWriteExpression(valueExpression: String) = throw UnsupportedOperationException()

            override fun generate(): ConverterWriter.Result {
                return ConverterWriter.Result(emptyList(), listOf(additionalProperties))
            }
        }
        else null
    }
}
