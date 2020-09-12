package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class IntegerConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "integer"
        override fun provide(basePackage: String) = IntegerConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.math.BigInteger"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.IntegerConverter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.IntegerConverter.WRITER"
            override fun stringParseExpression(valueExpression: String) = "new java.math.BigInteger($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}