package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema) =
            if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date" || jsonSchema.format == "local-date"))
                object : Converter {
                    override val jsonSchema = jsonSchema
                    override fun valueType() = "java.time.LocalDate"
                    override fun extraAnnotations() = "@com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)"
                    override fun stringParseExpression(valueExpression: String) = "java.time.LocalDate.parse($valueExpression)"
                    override fun stringWriteExpression(valueExpression: String) = "$valueExpression.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)"
                    override fun output() = ConverterOutput.EMPTY
                }
            else null
}
