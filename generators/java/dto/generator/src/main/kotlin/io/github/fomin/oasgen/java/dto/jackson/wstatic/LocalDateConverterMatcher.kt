package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "local-date"
        override fun provide(basePackage: String) = LocalDateConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date" || jsonSchema.format == "local-date"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDate"
                override fun parseExpression(valueExpression: String) =
                    "io.github.fomin.oasgen.LocalDateConverter.parse($valueExpression)"
                override fun writeExpression(valueExpression: String) =
                    "io.github.fomin.oasgen.LocalDateConverter.write(jsonGenerator, $valueExpression)"
                override fun stringParseExpression(valueExpression: String) = "java.time.LocalDate.parse($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)"
                override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
            }
        else null
    }
}
