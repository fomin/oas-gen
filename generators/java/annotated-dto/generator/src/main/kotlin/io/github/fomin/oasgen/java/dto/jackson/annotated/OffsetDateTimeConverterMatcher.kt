package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class OffsetDateTimeConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema) =
            if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "date-time")
                object : Converter {
                    override val jsonSchema = jsonSchema
                    override fun valueType() = "java.time.OffsetDateTime"
                    override fun extraAnnotations() = "@com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)"
                    override fun output() = ConverterOutput.EMPTY
                }
            else null
}
