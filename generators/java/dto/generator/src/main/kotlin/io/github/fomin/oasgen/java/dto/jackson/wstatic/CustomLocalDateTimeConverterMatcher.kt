package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class CustomLocalDateTimeConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "custom-local-date-time"
        override fun provide(basePackage: String) = CustomLocalDateTimeConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "custom-local-date-time")
            object : ConverterWriter {
                val pattern = jsonSchema.fragment["datePattern"].asString()
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDateTime"
                override fun parseExpression(valueExpression: String) =
                    "io.github.fomin.oasgen.LocalDateTimeConverter.parse($valueExpression, \"$pattern\")"
                override fun writeExpression(valueExpression: String) =
                    "io.github.fomin.oasgen.LocalDateTimeConverter.write(jsonGenerator, \"$pattern\", $valueExpression)"
                override fun stringParseExpression(valueExpression: String) = "java.time.LocalDateTime.parse($valueExpression, io.github.fomin.oasgen.DateTimeFormatterCache.get(\"$pattern\"))"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.format(io.github.fomin.oasgen.DateTimeFormatterCache.get(\"$pattern\"))"
                override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
            }
        else null
    }
}