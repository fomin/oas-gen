package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class StringConverterMatcher : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.Scalar.STRING -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = "java.lang.String"
                override fun parserCreateExpression() = "io.github.fomin.oasgen.ScalarParser.createStringParser()"
                override fun writerCreateExpression() = "io.github.fomin.oasgen.ScalarWriter.STRING_WRITER"
                override fun generate() = ConverterWriter.Result(null, emptyList())
            }
            else -> null
        }
    }
}