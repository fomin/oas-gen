package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema

class ConverterRegistry(private val converterMatcher: ConverterMatcher) {
    operator fun get(jsonSchema: JsonSchema): ConverterWriter {
        return converterMatcher.match(this, jsonSchema) ?:
        error("Can't find converter for schema $jsonSchema")
    }
}