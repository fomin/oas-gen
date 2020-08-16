package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.*

interface JsonConverter {
    fun toJson(valueExpression: String): String
    fun fromJson(): String
    fun content(): String?
}

interface TypeConverter {
    fun type(): String
    fun content(): String?
    fun importDeclarations(): List<ImportDeclaration>
    fun innerSchemas(): List<JsonSchema>
    val jsonConverter: JsonConverter?
}

interface TypeConverterMatcher {
    fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter?
}

class TypeConverterRegistry(private val converterMatcher: TypeConverterMatcher) {
    operator fun get(jsonSchema: JsonSchema): TypeConverter {
        return converterMatcher.match(this, jsonSchema) ?:
        error("Can't find converter for schema $jsonSchema")
    }
}
