package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import org.junit.jupiter.api.Test
import java.io.File

internal class ReactorNettyServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                ReactorNettyServerWriter("com.example.dto", "com.example.routes", emptyList()),
                File("build/test-schemas/openapi"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}