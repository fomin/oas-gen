package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.*
import org.junit.jupiter.api.Test
import java.io.File

internal class EnumConverterMatcherTest {

    @Test
    fun `matches object`() {
        val enumConverterMatcher = EnumConverterMatcher("com.example")
        val converterRegistry = ConverterRegistry(
            CompositeConverterMatcher(
                listOf(
                    enumConverterMatcher
                )
            )
        )
        val fragmentRegistry = FragmentRegistry(
            InMemoryContentLoader(
                mapOf(
                    "enumDto.yaml" to
                            """|type: string
                               |enum:
                               |  - value1
                               |  - value2
                            """.trimMargin()
                )
            )
        )
        val jsonSchema = JsonSchema(fragmentRegistry.get(Reference.root("enumDto.yaml")), null)
        enumConverterMatcher.match(converterRegistry, jsonSchema)
            ?: error("Expected non-null value")
    }

    @Test
    fun `simple enum`() {
        val enumConverterMatcher = EnumConverterMatcher("com.example.enumdto")
        val converterRegistry = ConverterRegistry(
            CompositeConverterMatcher(
                listOf(
                    enumConverterMatcher
                )
            )
        )
        jsonSchemaTestCase(
            JavaDtoWriter(converterRegistry),
            File("build/test-schemas/dto/enum"),
            "enum-dto.yaml",
            File("../expected-dto/src/enum/java")
        )
    }

}
