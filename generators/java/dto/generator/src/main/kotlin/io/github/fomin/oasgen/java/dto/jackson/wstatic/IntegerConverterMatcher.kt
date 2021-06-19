package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class IntegerConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "integer"
        override fun provide(dtoPackage: String, routesPackage: String) = IntegerConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.math.BigInteger"
            override fun parseExpression(valueExpression: String) =
                "io.github.fomin.oasgen.IntegerConverter.parse($valueExpression)"
            override fun writeExpression(jsonGeneratorName: String, valueExpression: String) =
                "io.github.fomin.oasgen.IntegerConverter.write($jsonGeneratorName, $valueExpression)"
            override fun stringParseExpression(valueExpression: String) = "new java.math.BigInteger($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
        }
        else null
    }
}