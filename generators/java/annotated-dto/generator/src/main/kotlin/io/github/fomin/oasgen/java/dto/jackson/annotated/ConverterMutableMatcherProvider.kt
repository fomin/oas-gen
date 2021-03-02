package io.github.fomin.oasgen.java.dto.jackson.annotated

class ConverterMutableMatcherProvider : ConverterMatcherProvider {
    override val id = "mutable"

    override fun provide(basePackage: String): ConverterMatcher {
        return CompositeConverterMatcher(
                listOf(
                        OffsetDateTimeConverterMatcher(),
                        LocalDateConverterMatcher(),
                        CustomLocalDateTimeConverterMatcher(),
                        LocalDateTimeConverterMatcher(),
                        ArrayConverterMatcher(),
                        MapConverterMatcher(),
                        MutableObjectConverterMatcher(basePackage),
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
