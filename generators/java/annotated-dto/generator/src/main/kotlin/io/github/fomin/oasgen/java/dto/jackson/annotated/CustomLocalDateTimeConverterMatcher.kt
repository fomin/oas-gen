package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class CustomLocalDateTimeConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "custom-local-date-time")
            object : Converter {
                val pattern = jsonSchema.fragment["datePattern"].asString()
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.time.LocalDateTime"
                override fun extraAnnotations() = "@com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern=\"$pattern\")"
                override fun output() = ConverterOutput.EMPTY
            }
        else null
    }
}
