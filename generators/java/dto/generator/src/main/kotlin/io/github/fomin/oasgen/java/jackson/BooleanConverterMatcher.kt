package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class BooleanConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "boolean"
        override fun provide(basePackage: String) = BooleanConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.BOOLEAN) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Boolean"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.BooleanConverter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.BooleanConverter.WRITER"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}