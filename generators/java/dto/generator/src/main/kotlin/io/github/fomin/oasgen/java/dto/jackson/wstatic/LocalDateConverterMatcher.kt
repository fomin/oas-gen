package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "local-date"
        override fun provide(basePackage: String, baseClass: String?, baseInterface: String?) = LocalDateConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date" || jsonSchema.format == "local-date"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDate"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.LocalDateConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.LocalDateConverter.WRITER"
                override fun stringParseExpression(valueExpression: String) = "java.time.LocalDate.parse($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
