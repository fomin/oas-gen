package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class DoubleConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "double"
        override fun provide(basePackage: String, baseClass: String?, baseInterface: String?) = DoubleConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.NUMBER && jsonSchema.format == "double") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Double"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.DoubleConverter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.DoubleConverter.WRITER"
            override fun stringParseExpression(valueExpression: String) = "java.lang.Double.parseDouble($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}