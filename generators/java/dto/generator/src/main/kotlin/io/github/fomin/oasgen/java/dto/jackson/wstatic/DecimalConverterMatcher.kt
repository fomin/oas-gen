package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class DecimalConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "decimal"
        override fun provide(basePackage: String) = DecimalConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "decimal") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.math.BigDecimal"
            override fun parserCreateExpression() = "io.github.fomin.oasgen.DecimalConverter.createParser()"
            override fun writerCreateExpression() = "io.github.fomin.oasgen.DecimalConverter.WRITER"
            override fun stringParseExpression(valueExpression: String) = "new java.math.BigDecimal($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toPlainString()"
            override fun generate() = ConverterWriter.Result(null, emptyList())
        }
        else null
    }
}