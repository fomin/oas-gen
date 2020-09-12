package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class StringConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "string"
        override fun provide(basePackage: String) = StringConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.STRING -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.lang.String"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.StringConverter.createParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.StringConverter.WRITER"
                override fun stringParseExpression(valueExpression: String) = valueExpression;
                override fun stringWriteExpression(valueExpression: String) = valueExpression;
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
            else -> null
        }
    }
}