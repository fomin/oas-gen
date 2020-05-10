package io.github.fomin.oasgen.java.rest.operations

import io.github.fomin.oasgen.testCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSpringRestOperationsWriterTest {
    @Test
    fun `generator should create expected output`() {
        testCase(
                JavaSpringRestOperationsWriter("com.example", emptyList()),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-client/src/main/java")
        )
    }
}
