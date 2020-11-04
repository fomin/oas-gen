package io.github.fomin.oasgen.typescript.dto

import java.util.*

interface TypeConverterMatcherProvider {
    val id: String
    fun provide(): TypeConverterMatcher

    class Default : TypeConverterMatcherProvider {
        override val id = "default"

        override fun provide() = CompositeTypeConverterMatcher(
                listOf(
                        OffsetDateTimeConverterMatcher(),
                        LocalDateConverterMatcher(),
                        LocalDateTimeConverterMatcher(),
                        StringEnumConverterMatcher(DefaultNamingStrategy()),
                        MapConverterMatcher(),
                        ArrayConverterMatcher(),
                        BooleanConverterMatcher(),
                        NumberConverterMatcher(),
                        IntegerConverterMatcher(),
                        StringConverterMatcher(),
                        ObjectConverterMatcher(DefaultNamingStrategy())
                )
        )
    }

    companion object {
        fun provide(converterIds: List<String>): TypeConverterMatcher {
            if (converterIds.isEmpty()) {
                return Default().provide()
            } else {
                val converterMatcherProviders = mutableMapOf<String, TypeConverterMatcherProvider>()
                val serviceLoader = ServiceLoader.load(TypeConverterMatcherProvider::class.java)
                val iterator = serviceLoader.iterator()
                while (iterator.hasNext()) {
                    val converterMatcherProvider = iterator.next()
                    converterMatcherProviders[converterMatcherProvider.id] = converterMatcherProvider
                }

                val converterMatchers = converterIds.map { converterId ->
                    val converterMatcherProvider = converterMatcherProviders[converterId]
                            ?: error("Can't find converter with id $converterId")
                    converterMatcherProvider.provide()
                }
                return CompositeTypeConverterMatcher(converterMatchers)
            }
        }
    }
}
