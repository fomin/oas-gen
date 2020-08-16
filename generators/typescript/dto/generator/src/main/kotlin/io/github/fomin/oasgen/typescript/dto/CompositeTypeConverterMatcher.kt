package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema

class CompositeTypeConverterMatcher(
        private val typeConverterMatchers: List<TypeConverterMatcher>
) : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        typeConverterMatchers.forEach { typeConverterMatcher ->
            val converterWriter = typeConverterMatcher.match(typeConverterRegistry, jsonSchema)
            if (converterWriter != null) return converterWriter
        }
        return null
    }
}
