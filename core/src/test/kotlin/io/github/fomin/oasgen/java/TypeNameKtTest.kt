package io.github.fomin.oasgen.java

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TypeNameKtTest {

    // ---- toUpperSnakeCase tests
    @Test
    fun `upper snake case from multiple parts`() {
        assertEquals("PART1_PART2_PART3", toUpperSnakeCase("part1", "part2", "part3"))
    }

    @Test
    fun `upper snake case from dashed`() {
        assertEquals("PART1_PART2_PART3", toUpperSnakeCase("part1-part2-part3"))
    }

    @Test
    fun `upper snake case from camel case`() {
        assertEquals("PART1_PART2_PART3", toUpperSnakeCase("part1Part2Part3"))
    }

    @Test
    fun `upper snake case from upper snake case`() {
        assertEquals("PART1_PART2_PART3", toUpperSnakeCase("PART1_PART2_PART3"))
    }

    @Test
    fun `upper snake case from lower snake case`() {
        assertEquals("PART1_PART2_PART3", toUpperSnakeCase("part1_part2_part3"))
    }

    @Test
    fun `upper snake case starting with digit`() {
        assertEquals("$1_PART", toUpperSnakeCase("1_PART"))
    }

    @Test
    fun `upper snake case with spaces`() {
        assertEquals("PART1_PART2", toUpperSnakeCase("part1 part2"))
    }

    // ---- toUpperCamelCase tests
    @Test
    fun `upper camel case from multiple parts`() {
        assertEquals("Part1Part2Part3", toUpperCamelCase("part1", "part2", "part3"))
    }

    @Test
    fun `upper camel case from dashed`() {
        assertEquals("Part1Part2Part3", toUpperCamelCase("part1-part2-part3"))
    }

    @Test
    fun `upper camel case from camel case`() {
        assertEquals("Part1Part2Part3", toUpperCamelCase("part1Part2Part3"))
    }

    @Test
    fun `upper camel case from upper snake case`() {
        assertEquals("Part1Part2Part3", toUpperCamelCase("PART1_PART2_PART3"))
    }

    @Test
    fun `upper camel case from lower snake case`() {
        assertEquals("Part1Part2Part3", toUpperCamelCase("part1_part2_part3"))
    }

    @Test
    fun `upper camel case starting with digit`() {
        assertEquals("$1Part", toUpperCamelCase("1_PART"))
    }

    @Test
    fun `upper camel case with spaces`() {
        assertEquals("Part1Part2", toUpperCamelCase("part1 part2"))
    }
}
