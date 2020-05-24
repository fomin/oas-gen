package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.OutputFile

class ConverterRegistry(private val converterMatcher: ConverterMatcher) {
    operator fun get(jsonSchema: JsonSchema): Converter {
        return converterMatcher.match(this, jsonSchema) ?:
        error("Can't find converter for schema $jsonSchema")
    }
}

interface ConverterMatcher {
    fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter?
}

interface Converter {
    val jsonSchema: JsonSchema
    fun valueType(): String
    fun extraAnnotations(): String?
    fun output(): ConverterOutput
}

data class ConverterOutput(val outputFile: OutputFile?, val jsonSchemas: List<JsonSchema>) {
    companion object {
        val EMPTY = ConverterOutput(null, emptyList())
    }
}
