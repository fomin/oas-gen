package io.github.fomin.oasgen.java.dto.jackson.wstatic

import java.util.*

interface ConverterMatcherProvider {
    val id: String
    fun provide(dtoPackage: String, routesPackage: String): ConverterMatcher

    class Default : ConverterMatcherProvider {
        override val id = "default"

        override fun provide(dtoPackage: String, routesPackage: String): ConverterMatcher {
            return CompositeConverterMatcher(
                    listOf(
                            OffsetDateTimeConverterMatcher(),
                            LocalDateConverterMatcher(),
                            CustomLocalDateTimeConverterMatcher(),
                            LocalDateTimeConverterMatcher(),
                            ArrayConverterMatcher(),
                            MapConverterMatcher(),
                            ObjectConverterMatcher(dtoPackage, routesPackage),
                            Int32ConverterMatcher(),
                            Int64ConverterMatcher(),
                            IntegerConverterMatcher(),
                            DoubleConverterMatcher(),
                            NumberConverterMatcher(),
                            BooleanConverterMatcher(),
                            EnumConverterMatcher(dtoPackage, routesPackage),
                            DecimalConverterMatcher(),
                            StringConverterMatcher()
                    )
            )
        }
    }

    class MutableDefault : ConverterMatcherProvider {
        override val id = "mutable"

        override fun provide(dtoPackage: String, routesPackage: String): ConverterMatcher {
            return CompositeConverterMatcher(
                    listOf(
                            OffsetDateTimeConverterMatcher(),
                            LocalDateConverterMatcher(),
                            CustomLocalDateTimeConverterMatcher(),
                            LocalDateTimeConverterMatcher(),
                            ArrayConverterMatcher(),
                            MapConverterMatcher(),
                            MutableObjectConverterMatcher(dtoPackage, routesPackage),
                            Int32ConverterMatcher(),
                            Int64ConverterMatcher(),
                            IntegerConverterMatcher(),
                            DoubleConverterMatcher(),
                            NumberConverterMatcher(),
                            BooleanConverterMatcher(),
                            EnumConverterMatcher(dtoPackage, routesPackage),
                            DecimalConverterMatcher(),
                            StringConverterMatcher()
                    )
            )
        }
    }

    companion object {

        fun provide(dtoPackage: String, routesPackage: String, converterIds: List<String>): ConverterMatcher {
            if (converterIds.isEmpty()) {
                return Default().provide(dtoPackage, routesPackage)
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
                    converterMatcherProvider.provide(dtoPackage, routesPackage)
                }
                return CompositeConverterMatcher(converterMatchers)
            }
        }
    }
}
