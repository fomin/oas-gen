package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.openApiTestCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSpringRestOperationsWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                JavaSpringRestOperationsWriter("com.example", emptyList()),
                File("../../../../simple-schema/resttemplate"),
                "simple.yaml",
                File("../expected-client/src/main/java")
        )
    }
}
