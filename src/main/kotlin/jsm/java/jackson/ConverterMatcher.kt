package jsm.java.jackson

import jsm.JsonSchema

interface ConverterMatcher {
    fun match(jsonSchema: JsonSchema): ConverterWriter?
}