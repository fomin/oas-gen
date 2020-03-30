package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class BooleanConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.BOOLEAN) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.lang.Boolean"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.BooleanConverter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.BooleanConverter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}