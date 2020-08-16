package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class OffsetDateTimeConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "date-time"
        override fun provide() = OffsetDateTimeConverterMatcher()
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when {
        jsonSchema.type == JsonType.Scalar.STRING && jsonSchema.format == "date-time" -> object : TypeConverter {
            override fun type() = "OffsetDateTime"
            override fun content(): String? = null
            override fun importDeclarations() = listOf(
                    ImportDeclaration("DateTimeFormatter", "@js-joda/core"),
                    ImportDeclaration("OffsetDateTime", "@js-joda/core")
            )
            override fun innerSchemas() = listOf<JsonSchema>()
            override val jsonConverter = object : JsonConverter {
                override fun toJson(valueExpression: String) = "($valueExpression as OffsetDateTime).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)"
                override fun fromJson() = "OffsetDateTime.parse"
                override fun content(): String? = null
            }

        }
        else -> null
    }
}