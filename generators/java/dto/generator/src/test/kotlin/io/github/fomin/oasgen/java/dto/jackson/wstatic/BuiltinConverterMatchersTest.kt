package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.TestUtils.Companion.assertOutputFilesEquals
import org.junit.jupiter.api.Test
import java.io.File

class BuiltinConverterMatchersTest {

    class TestCase(
        val suffix: String,
        val schemaContent: String,
        val converterMatcher: ConverterMatcher
    )

    @Test
    fun `check builtin converter matchers`() {
        val testCases = listOf(
            TestCase(
                "Decimal",
                """|type: string
                   |format: decimal
                """.trimMargin(),
                DecimalConverterMatcher()
            ),
            TestCase(
                "Int32",
                """|type: integer
                   |format: int32
                """.trimMargin(),
                Int32ConverterMatcher()
            )
        )

        val testMethods = testCases.map { testCase ->
            val converterRegistry = ConverterRegistry(
                CompositeConverterMatcher(
                    listOf(
                        testCase.converterMatcher
                    )
                )
            )
            val fragmentRegistry = FragmentRegistry(InMemoryContentLoader(mapOf("schema.yaml" to testCase.schemaContent)))
            val jsonSchema = JsonSchema(fragmentRegistry.get(Reference.root("schema.yaml")), null)
            val converterWriter = testCase.converterMatcher.match(converterRegistry, jsonSchema)
                ?: error("matcher should math schema, test case suffix = ${testCase.suffix}")
            val valueType = converterWriter.valueType()
            """|public static $valueType parse${testCase.suffix}(JsonNode jsonNode) {
               |    return ${converterWriter.parseExpression("jsonNode")};
               |}
               |
               |public static void write${testCase.suffix}(JsonGenerator jsonGenerator, $valueType value) throws IOException {
               |    ${converterWriter.writeExpression("jsonGenerator", "value")};
               |}
               |
            """.trimMargin()
        }


        val expectedContent = File("../expected-dto/src/builtin/java/com/example/builtin/Builtin.java").readText()

        val actualOutput =
            """|package com.example.builtin;
               |
               |import com.fasterxml.jackson.core.JsonGenerator;
               |import com.fasterxml.jackson.databind.JsonNode;
               |
               |import java.io.IOException;
               |
               |public class Builtin {
               |
               |    ${testMethods.indentWithMargin(1)}
               |
               |}
               |
            """.trimMargin()

        assertOutputFilesEquals(
            "Builtin converters should match expected output",
            listOf(OutputFile("com/example/builtin", expectedContent, OutputFileType.DTO)),
            listOf(OutputFile("com/example/builtin", actualOutput, OutputFileType.DTO))
        )
    }
}