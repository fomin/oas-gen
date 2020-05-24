package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class Int32ConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema) =
            if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int32")
                object : Converter {
                    override val jsonSchema = jsonSchema
                    override fun valueType() = "java.lang.Integer"
                    override fun extraAnnotations(): String? = null
                    override fun output() = ConverterOutput.EMPTY
                }
            else null
}
