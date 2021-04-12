// TODO FIX
//package io.github.fomin.oasgen.java.dto.jackson.annotated
//
//import io.github.fomin.oasgen.JsonSchema
//import io.github.fomin.oasgen.JsonType
//
//class BinaryConverterMatcher : ConverterMatcher {
//    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema) =
//            if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.format == "binary")
//                object : Converter {
//                    override val jsonSchema = jsonSchema
//                    override fun valueType() = "org.springframework.core.io.Resource"
//                    override fun extraAnnotations(): String? = null
//                    override fun stringParseExpression(valueExpression: String) = error("Not implemented")
//                    override fun stringWriteExpression(valueExpression: String) = ""
//                    override fun output() = ConverterOutput.EMPTY
//                }
//            else null
//}