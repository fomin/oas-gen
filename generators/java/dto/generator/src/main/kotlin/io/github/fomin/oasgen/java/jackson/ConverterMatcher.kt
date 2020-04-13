package io.github.fomin.oasgen.java.jackson

import io.github.fomin.oasgen.JsonSchema

interface ConverterMatcher {
    fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter?
}