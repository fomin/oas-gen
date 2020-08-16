package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class ArrayConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "array"
        override fun provide() = ArrayConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        return if (jsonSchema.type == JsonType.ARRAY) {
            object : TypeConverter {
                val itemsSchema = jsonSchema.items() ?: error("items is required in $jsonSchema")
                val itemsTypeConverter = typeConverterRegistry[itemsSchema]
                val itemsJsonConverter = itemsTypeConverter.jsonConverter

                override fun type() = "readonly ${itemsTypeConverter.type()}[]"

                override fun content(): String? = null

                override fun importDeclarations() = listOf<ImportDeclaration>()

                override fun innerSchemas() = listOf(itemsSchema)

                override val jsonConverter = if (itemsJsonConverter != null) {
                    object : JsonConverter {
                        override fun toJson(valueExpression: String) = "TODO"

                        override fun fromJson() = "TODO"

                        override fun content() = "TODO"
                    }
                } else {
                    null
                }

            }
        } else {
            null
        }
    }
}