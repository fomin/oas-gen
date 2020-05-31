package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.testCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSpringMvcServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        testCase(
                JavaSpringMvcServerWriter("com.example", emptyList()),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}
