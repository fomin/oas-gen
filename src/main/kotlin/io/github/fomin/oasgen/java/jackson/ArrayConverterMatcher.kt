package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

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
                        "new io.github.fomin.oasgen.ArrayParser<>(${converterRegistry[itemsSchema].parserCreateExpression(converterRegistry)})"

                override fun writerCreateExpression(converterRegistry: ConverterRegistry) =
                        "new io.github.fomin.oasgen.ArrayWriter<>(${converterRegistry[itemsSchema].writerCreateExpression(converterRegistry)})"

                override fun generate(converterRegistry: ConverterRegistry): ConverterWriter.Result {
                    return ConverterWriter.Result(null, listOf(itemsSchema))
                }
            }
            else -> null
        }
    }
}
