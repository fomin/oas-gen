package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class BooleanConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "boolean"
        override fun provide(basePackage: String, baseClass: String?, baseInterface: String?) = BooleanConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.BOOLEAN) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Boolean"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.BooleanConverter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.BooleanConverter.WRITER"
            override fun stringParseExpression(valueExpression: String) = "java.lang.Boolean.parseBoolean($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}