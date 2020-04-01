package io.github.fomin.oasgen

import io.github.fomin.oasgen.java.rest.operations.JavaSpringRestOperationsWriter
import io.github.fomin.oasgen.java.spring.mvc.JavaSrpingMvcServerWriter
import io.github.fomin.oasgen.typescript.axios.AxiosClientWriter
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

internal class OpenApiTestCases {

    class TestCase(
            val writer: Writer<OpenApiSchema>,
            val baseDir: File,
            val schemaPath: String,
            val outputDir: File
    )

    @ParameterizedTest
    @MethodSource("inputSource")
    fun testCases(testCase: TestCase) {
        val fragmentRegistry = FragmentRegistry(testCase.baseDir)
        val rootFragment = fragmentRegistry.get(Reference.root(testCase.schemaPath))
        val openApiSchema = OpenApiSchema(rootFragment, null)
        val actualOutputFiles = testCase.writer.write(listOf(openApiSchema))

        val outputDirUri = testCase.outputDir.toURI()
        val expectedOutputFiles = testCase.outputDir.walk().filter { it.isFile }.map {
            val relativePath = outputDirUri.relativize(it.toURI())
            OutputFile(relativePath.toString(), it.readText().replace("\r", ""))
        }.toList()

        TestUtils.assertOutputFilesEquals(
                "Failed test case '${testCase.baseDir}'",
                expectedOutputFiles,
                actualOutputFiles
        )
    }

    companion object {
        @JvmStatic
        private fun inputSource() = listOf(
                TestCase(
                        JavaSrpingMvcServerWriter("com.example"),
                        File("test-cases/schema"),
                        "simple.yaml",
                        File("test-cases/spring-mvc-server/src/expected/java")
                ),
                TestCase(
                        JavaSpringRestOperationsWriter("com.example"),
                        File("test-cases/schema"),
                        "simple.yaml",
                        File("test-cases/spring-rest-operations-client/src/expected/java")
                ),
                TestCase(
                        AxiosClientWriter("com.example"),
                        File("test-cases/schema"),
                        "simple.yaml",
                        File("test-cases/axios-client/expected/src")
                )
        )

    }
}
