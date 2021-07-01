package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class DecimalConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "decimal"
        override fun provide(dtoPackage: String, routesPackage: String) = DecimalConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "decimal") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.math.BigDecimal"
            override fun parseExpression(valueExpression: String, localVariableSuffix: Int) =
                "io.github.fomin.oasgen.DecimalConverter.parse($valueExpression)"
            override fun writeExpression(jsonGeneratorName: String, valueExpression: String, localVariableSuffix: Int) =
                "io.github.fomin.oasgen.DecimalConverter.write($jsonGeneratorName, $valueExpression)"
            override fun stringParseExpression(valueExpression: String) = "new java.math.BigDecimal($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toPlainString()"
            override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
        }
        else null
    }
}