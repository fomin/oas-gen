package io.github.fomin.oasgen.java.destruction.test

import io.github.fomin.oasgen.openApiTestCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaDestructionTestWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                JavaDestructionTestWriter("com.example.dto", "com.example.routes", emptyList()),
                File("build/test-schemas/openapi"),
                "simple.yaml",
                File("../expected-test/src/main/java")
        )
    }
}
