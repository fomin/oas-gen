package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class ArrayConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.ARRAY -> object : ConverterWriter {
                private val itemsSchema = jsonSchema.items()
                        ?: error("array schema should have items schema at $jsonSchema")

                override val jsonSchema = jsonSchema

                override fun valueType(converterRegistry: ConverterRegistry) =
                        "java.util.List<${converterRegistry[itemsSchema].valueType(converterRegistry)}>"

                override fun parserCreateExpression(converterRegistry: ConverterRegistry) =
                        "new jsm.ArrayParser<>(${converterRegistry[itemsSchema].parserCreateExpression(converterRegistry)})"

                override fun writerCreateExpression(converterRegistry: ConverterRegistry) =
                        "new jsm.ArrayWriter<>(${converterRegistry[itemsSchema].writerCreateExpression(converterRegistry)})"

                override fun generate(converterRegistry: ConverterRegistry): ConverterWriter.Result {
                    return ConverterWriter.Result(null, listOf(itemsSchema))
                }
            }
            else -> null
        }
    }
}
