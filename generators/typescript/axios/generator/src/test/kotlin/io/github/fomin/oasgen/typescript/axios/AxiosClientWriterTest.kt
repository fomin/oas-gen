package io.github.fomin.oasgen.typescript.axios

import io.github.fomin.oasgen.testCase
import org.junit.jupiter.api.Test
import java.io.File

internal class AxiosClientWriterTest {
    @Test
    fun `generator should create expected output`() {
        testCase(
                AxiosClientWriter("com.example"),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-client/src")
        )
    }

}