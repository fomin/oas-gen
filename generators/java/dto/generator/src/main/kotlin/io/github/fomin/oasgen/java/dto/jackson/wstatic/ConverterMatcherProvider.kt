package io.github.fomin.oasgen.java.dto.jackson.wstatic

import java.util.*

interface ConverterMatcherProvider {
    val id: String
    fun provide(basePackage: String, baseClass: String?, baseInterface: String?): ConverterMatcher


    class Default : ConverterMatcherProvider {
        override val id = "default"

        override fun provide(basePackage: String, baseClass: String?, baseInterface: String?): ConverterMatcher {
            return CompositeConverterMatcher(
                    listOf(
                            OffsetDateTimeConverterMatcher(),
                            LocalDateConverterMatcher(),
                            CustomLocalDateTimeConverterMatcher(),
                            LocalDateTimeConverterMatcher(),
                            ArrayConverterMatcher(),
                            MapConverterMatcher(),
                            ObjectConverterMatcher(basePackage, baseClass, baseInterface),
                            Int32ConverterMatcher(),
                            Int64ConverterMatcher(),
                            IntegerConverterMatcher(),
                            DoubleConverterMatcher(),
                            NumberConverterMatcher(),
                            BooleanConverterMatcher(),
                            EnumConverterMatcher(basePackage),
                            DecimalConverterMatcher(),
                            StringConverterMatcher()
                    )
            )
        }
    }

    companion object {

        fun provide(basePackage: String, converterIds: List<String>, baseClass: String?, baseInterface: String?): ConverterMatcher {
            if (converterIds.isEmpty()) {
                return Default().provide(basePackage, baseClass, baseInterface)
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
                    converterMatcherProvider.provide(basePackage, baseClass, baseInterface)
                }
                return CompositeConverterMatcher(converterMatchers)
            }
        }
    }
}
