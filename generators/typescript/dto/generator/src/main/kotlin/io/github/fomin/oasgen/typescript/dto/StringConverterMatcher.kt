package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class StringConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "string"
        override fun provide() = StringConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.STRING -> object : TypeConverter {
            override fun type() = "string"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}