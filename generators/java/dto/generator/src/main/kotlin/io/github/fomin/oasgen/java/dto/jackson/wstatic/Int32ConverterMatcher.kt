package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType

class Int32ConverterMatcher : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "int32"
        override fun provide(dtoPackage: String, routesPackage: String) = Int32ConverterMatcher()
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.INTEGER && jsonSchema.format == "int32") object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = "java.lang.Integer"
            override fun parseExpression(valueExpression: String) =
                "io.github.fomin.oasgen.Int32Converter.parse($valueExpression)"
            override fun writeExpression(jsonGeneratorName: String, valueExpression: String) =
                "io.github.fomin.oasgen.Int32Converter.write($jsonGeneratorName, $valueExpression)"
            override fun stringParseExpression(valueExpression: String) = "java.lang.Integer.parseInt($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "$valueExpression.toString()"
            override fun generate() = ConverterWriter.Result(emptyList(), emptyList())
        }
        else null
    }
}