package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.openApiTestCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSpringMvcServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                JavaSpringMvcServerWriter("com.example", emptyList()),
                File("../../../../simple-schema/mvc"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}
