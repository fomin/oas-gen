package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.openApiTestCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSpringMvcServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                JavaSpringMvcServerWriter("com.example.dto", "com.example.routes", emptyList()),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}
