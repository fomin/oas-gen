package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class LocalDateTimeConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "local-date-time")
            object : Converter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDateTime"
                override fun extraAnnotations() = "@com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)"
                override fun stringParseExpression(valueExpression: String) = "java.time.LocalDateTime.parse($valueExpression)"
                override fun stringWriteExpression(valueExpression: String) = "$valueExpression.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)"
                override fun output() = ConverterOutput.EMPTY
            }
        else null
    }
}
