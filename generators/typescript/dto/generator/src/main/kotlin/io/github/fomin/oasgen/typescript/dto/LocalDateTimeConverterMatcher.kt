package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateTimeConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "local-date-time"
        override fun provide() = LocalDateTimeConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when {
        jsonSchema.type == JsonType.Scalar.STRING && jsonSchema.format == "local-date-time" -> object : TypeConverter {
            override fun type() = "LocalDateTime"
            override fun content(): String? = null
            override fun importDeclarations() = listOf(
                    ImportDeclaration("DateTimeFormatter", "@js-joda/core"),
                    ImportDeclaration("LocalDateTime", "@js-joda/core")
            )
            override fun innerSchemas() = listOf<JsonSchema>()
            override val jsonConverter = object : JsonConverter {
                override fun toJson(valueExpression: String) = "($valueExpression as LocalDateTime).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)"
                override fun fromJson() = "LocalDateTime.parse"
                override fun content(): String? = null
            }

        }
        else -> null
    }
}