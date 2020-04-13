package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema

class ConverterRegistry(private val converterMatchers: List<ConverterMatcher>) {
    operator fun get(jsonSchema: JsonSchema): ConverterWriter {
        converterMatchers.forEach { converterMatcher ->
            val converterWriter = converterMatcher.match(this, jsonSchema)
            if (converterWriter != null) return converterWriter
        }
        error("Can't find converter for schema $jsonSchema")
    }
}