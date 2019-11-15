package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType

class LocalDateConverterMatcher : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && (jsonSchema.format == "date" || jsonSchema.format == "local-date"))
            object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType(converterRegistry: ConverterRegistry) = "java.time.LocalDate"
                override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "jsm.LocalDateConverter.createParser()"
                override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "jsm.LocalDateConverter.WRITER"
                override fun generate(converterRegistry: ConverterRegistry) = ConverterWriter.Result(null, emptyList())
            }
        else null
    }
}
