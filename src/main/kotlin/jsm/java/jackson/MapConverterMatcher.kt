package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class MapConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        val additionalProperties = jsonSchema.additionalProperties()
        return if (jsonSchema.type is JsonType.OBJECT && additionalProperties != null) object : ConverterWriter {
            override val jsonSchema = jsonSchema

            override fun valueType(converterRegistry: ConverterRegistry) =
                    "java.util.Map<java.lang.String, ${converterRegistry[additionalProperties].valueType(converterRegistry)}>"

            override fun parserCreateExpression(converterRegistry: ConverterRegistry) =
                    "new jsm.MapParser<>(${converterRegistry[additionalProperties].parserCreateExpression(converterRegistry)})"

            override fun writerCreateExpression(converterRegistry: ConverterRegistry) =
                    "new jsm.MapWriter<>(${converterRegistry[additionalProperties].writerCreateExpression(converterRegistry)})"

            override fun generate(converterRegistry: ConverterRegistry): ConverterWriter.Result {
                return ConverterWriter.Result(null, listOf(additionalProperties))
            }
        }
        else null
    }
}
