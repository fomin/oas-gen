package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class MapConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        val additionalProperties = jsonSchema.additionalProperties()
        return if (jsonSchema.type is JsonType.OBJECT && additionalProperties != null) object : ConverterWriter {
            override val jsonSchema = jsonSchema

            override fun valueType(converterRegistry: ConverterRegistry) =
                    "java.util.Map<java.lang.String, ${converterRegistry[additionalProperties].valueType(converterRegistry)}>"

            override fun parserCreateExpression(converterRegistry: ConverterRegistry) =
                    "new io.github.fomin.oasgen.MapParser<>(${converterRegistry[additionalProperties].parserCreateExpression(converterRegistry)})"

            override fun writerCreateExpression(converterRegistry: ConverterRegistry) =
                    "new io.github.fomin.oasgen.MapWriter<>(${converterRegistry[additionalProperties].writerCreateExpression(converterRegistry)})"

            override fun generate(converterRegistry: ConverterRegistry): ConverterWriter.Result {
                return ConverterWriter.Result(null, listOf(additionalProperties))
            }
        }
        else null
    }
}
