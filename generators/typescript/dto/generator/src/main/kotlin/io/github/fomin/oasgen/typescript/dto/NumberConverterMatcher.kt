package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class NumberConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "number"
        override fun provide() = NumberConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.NUMBER -> object : TypeConverter {
            override fun type() = "number"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}