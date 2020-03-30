package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class Int32ConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int32") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.lang.Integer"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.Int32Converter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.Int32Converter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}