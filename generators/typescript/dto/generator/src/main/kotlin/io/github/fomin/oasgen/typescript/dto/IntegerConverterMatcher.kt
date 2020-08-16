package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class IntegerConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "integer"
        override fun provide() = IntegerConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.INTEGER -> object : TypeConverter {
            override fun type() = "bigint"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}