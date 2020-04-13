package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.*
import org.junit.jupiter.api.Test
import java.io.File

internal class ReactorNettyServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        testCase(
                ReactorNettyServerWriter("com.example"),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}