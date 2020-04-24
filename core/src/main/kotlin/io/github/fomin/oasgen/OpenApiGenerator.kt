package io.github.fomin.oasgen

import java.io.File
import java.util.*

fun openApiGenerate(generatorId: String, baseDir: File, outputDir: File, schemaPath: String, namespace: String) {
    val serviceLoader = ServiceLoader.load(OpenApiWriterProvider::class.java)
    val providerMap = serviceLoader.iterator()
            .asSequence()
            .fold(mutableMapOf<String, OpenApiWriterProvider>()) { acc, openApiWriterProvider ->
                val previousValue = acc.put(openApiWriterProvider.id, openApiWriterProvider)
                if (previousValue != null) error("Found duplicate OpenAPI generator id '${openApiWriterProvider.id}'")
                acc
            }
    val openApiWriterProvider = providerMap[generatorId] ?: error("Can't find generator $generatorId")

    val fragmentRegistry = FragmentRegistry(baseDir)
    val openApiSchema = OpenApiSchema(fragmentRegistry.get(Reference.root(schemaPath)), null)

    val writer = openApiWriterProvider.provide(namespace)

    val outputFiles = writer.write(listOf(openApiSchema))
    outputDir.mkdirs()
    outputFiles.forEach { outputFile ->
        val generatedFile = File(outputDir, outputFile.path)
        generatedFile.parentFile.mkdirs()
        generatedFile.writeText(outputFile.content)
    }

}
