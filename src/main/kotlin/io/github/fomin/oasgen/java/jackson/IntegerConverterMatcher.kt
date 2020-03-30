package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class IntegerConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.math.BigInteger"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.IntegerConverter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.IntegerConverter.WRITER"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}