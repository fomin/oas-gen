package io.github.fomin.oasgen

import java.io.File
import java.util.*

fun openApiGenerate(
    generatorId: String,
    baseDir: File,
    dtoOutputDir: File,
    routeOutputDir: File,
    schemaPath: String,
    dtoNamespace: String,
    routeNamespace: String,
    dtoBaseClass: String?,
    dtoBaseInterface: String?,
    converterIds: List<String>
) {
    val serviceLoader = ServiceLoader.load(OpenApiWriterProvider::class.java)
    val providerMap = serviceLoader.iterator()
            .asSequence()
            .fold(mutableMapOf<String, OpenApiWriterProvider>()) { acc, openApiWriterProvider ->
                val previousValue = acc.put(openApiWriterProvider.id, openApiWriterProvider)
                if (previousValue != null) error("Found duplicate OpenAPI generator id '${openApiWriterProvider.id}'")
                acc
            }
    val openApiWriterProvider = providerMap[generatorId] ?: error("Can't find generator $generatorId")

    val fragmentRegistry = FragmentRegistry(FileContentLoader(baseDir))
    val openApiSchema = OpenApiSchema(fragmentRegistry.get(Reference.root(schemaPath)), null)
    val writer = openApiWriterProvider.provide(dtoNamespace, routeNamespace, converterIds, dtoBaseClass, dtoBaseInterface)

    val outputFiles = writer.write(listOf(openApiSchema))
    dtoOutputDir.mkdirs()
    if (routeOutputDir != null && dtoOutputDir != routeOutputDir) {
        routeOutputDir.mkdirs()
    }
    outputFiles.forEach { outputFile ->
        val outputDir = when (outputFile.type == OutputFileType.DTO) {
            true -> dtoOutputDir
            else -> routeOutputDir
        }
        val generatedFile = File(outputDir, outputFile.path)
        generatedFile.parentFile.mkdirs()
        generatedFile.writeText(outputFile.content)
    }

}
