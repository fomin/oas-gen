package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class IntegerConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = "java.math.BigInteger"
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.IntegerConverter.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "io.github.fomin.oasgen.IntegerConverter.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}