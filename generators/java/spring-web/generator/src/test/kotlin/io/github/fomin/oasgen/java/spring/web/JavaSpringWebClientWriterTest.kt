package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.openApiTestCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSpringWebClientWriterTest {

    /**
     * This test and affected files should not be changed - this is generator smoke test.
     * For generator tests please look at tests in package `io.github.fomin.oasgen.java.dto.jackson.wstatic`.
     */
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                JavaSpringWebClientWriter("com.example.dto", "com.example.routes", emptyList()),
                File("build/test-schemas/openapi"),
                "simple.yaml",
                File("../expected-client/src/main/java")
        )
    }
}
