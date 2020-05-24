package io.github.fomin.oasgen.java.dto.jackson.annotated

import java.util.*

interface ConverterMatcherProvider {
    val id: String
    fun provide(basePackage: String): ConverterMatcher

    class Default : ConverterMatcherProvider {
        override val id = "default"

        override fun provide(basePackage: String): ConverterMatcher {
            return CompositeConverterMatcher(
                    listOf(
                            OffsetDateTimeConverterMatcher(),
                            LocalDateConverterMatcher(),
                            LocalDateTimeConverterMatcher(),
                            ArrayConverterMatcher(),
                            MapConverterMatcher(),
                            ObjectConverterMatcher(basePackage),
                            Int32ConverterMatcher(),
                            Int64ConverterMatcher(),
                            IntegerConverterMatcher(),
                            NumberConverterMatcher(),
                            BooleanConverterMatcher(),
                            EnumConverterMatcher(basePackage),
                            StringConverterMatcher()
                    )
            )
        }
    }

    companion object {

        fun provide(basePackage: String, converterIds: List<String>): ConverterMatcher {
            if (converterIds.isEmpty()) {
                return Default().provide(basePackage)
            } else {
                val converterMatcherProviders = mutableMapOf<String, ConverterMatcherProvider>()
                val serviceLoader = ServiceLoader.load(ConverterMatcherProvider::class.java)
                val iterator = serviceLoader.iterator()
                while (iterator.hasNext()) {
                    val converterMatcherProvider = iterator.next()
                    converterMatcherProviders[converterMatcherProvider.id] = converterMatcherProvider
                }

                val converterMatchers = converterIds.map { converterId ->
                    val converterMatcherProvider = converterMatcherProviders[converterId]
                            ?: error("Can't find converter with id $converterId")
                    converterMatcherProvider.provide(basePackage)
                }
                return CompositeConverterMatcher(converterMatchers)
            }
        }
    }
}
