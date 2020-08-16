package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "date"
        override fun provide() = LocalDateConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when {
        jsonSchema.type == JsonType.Scalar.STRING && jsonSchema.format == "date" -> object : TypeConverter {
            override fun type() = "LocalDate"
            override fun content(): String? = null
            override fun importDeclarations() = listOf(
                    ImportDeclaration("DateTimeFormatter", "@js-joda/core"),
                    ImportDeclaration("LocalDate", "@js-joda/core")
            )
            override fun innerSchemas() = listOf<JsonSchema>()
            override val jsonConverter = object : JsonConverter {
                override fun toJson(valueExpression: String) = "($valueExpression as LocalDate).format(DateTimeFormatter.ISO_LOCAL_DATE)"
                override fun fromJson() = "LocalDate.parse"
                override fun content(): String? = null
            }

        }
        else -> null
    }
}