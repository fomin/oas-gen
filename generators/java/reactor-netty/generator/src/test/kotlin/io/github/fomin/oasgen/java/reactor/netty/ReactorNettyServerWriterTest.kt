package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import org.junit.jupiter.api.Test
import java.io.File

internal class ReactorNettyServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                ReactorNettyServerWriter("com.example", "com.example", emptyList()),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}