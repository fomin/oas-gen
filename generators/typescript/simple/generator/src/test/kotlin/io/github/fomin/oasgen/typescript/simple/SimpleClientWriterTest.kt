package io.github.fomin.oasgen.typescript.simple

import io.github.fomin.oasgen.testCase
import org.junit.jupiter.api.Test
import java.io.File

internal class SimpleClientWriterTest {
    @Test
    fun `generator should create expected output`() {
        testCase(
                SimpleClientWriter(),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-client/src")
        )
    }

}
