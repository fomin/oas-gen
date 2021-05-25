package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class BooleanConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "boolean"
        override fun provide(dtoPackage: String, routesPackage: String) = BooleanConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.BOOLEAN) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Boolean"
            override fun parseExpression(valueExpression: String) =
                "io.github.fomin.oasgen.BooleanConverter.parse($valueExpression)"
            override fun writeExpression(jsonGeneratorName: String, valueExpression: String) =
                "io.github.fomin.oasgen.BooleanConverter.write($jsonGeneratorName, $valueExpression)"
            override fun stringParseExpression(valueExpression: String) = "java.lang.Boolean.parseBoolean($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
        }
        else null
    }
}