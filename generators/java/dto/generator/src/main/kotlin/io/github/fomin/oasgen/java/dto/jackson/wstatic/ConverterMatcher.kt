package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema

interface ConverterMatcher {
    fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter?
}