package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class BooleanConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "boolean"
        override fun provide() = BooleanConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.BOOLEAN -> object : TypeConverter {
            override fun type() = "boolean"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}