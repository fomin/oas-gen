package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class Int64ConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int64") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Long"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.Int64Converter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.Int64Converter.WRITER"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}