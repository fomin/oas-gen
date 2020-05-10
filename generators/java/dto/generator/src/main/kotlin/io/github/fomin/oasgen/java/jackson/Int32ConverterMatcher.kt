package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class Int32ConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "int32"
        override fun provide(basePackage: String) = Int32ConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int32") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Integer"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.Int32Converter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.Int32Converter.WRITER"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}