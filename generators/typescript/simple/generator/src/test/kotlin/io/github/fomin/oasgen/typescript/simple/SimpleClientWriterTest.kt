package io.github.fomin.oasgen.typescript.simple

import io.github.fomin.oasgen.openApiTestCase
import org.junit.jupiter.api.Test
import java.io.File

internal class SimpleClientWriterTest {
    @Test
    fun `generator should create expected output`() {
        openApiTestCase(
                SimpleClientWriter(emptyList()),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-client/src")
        )
    }

}
