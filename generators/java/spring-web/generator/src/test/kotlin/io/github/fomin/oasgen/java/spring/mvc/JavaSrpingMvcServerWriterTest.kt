package io.github.fomin.oasgen.java.spring.mvc

import io.github.fomin.oasgen.testCase
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaSrpingMvcServerWriterTest {
    @Test
    fun `generator should create expected output`() {
        testCase(
                JavaSrpingMvcServerWriter("com.example"),
                File("../../../../simple-schema"),
                "simple.yaml",
                File("../expected-server/src/main/java")
        )
    }
}
