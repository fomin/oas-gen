@file:JvmName("BuiltinTypeScriptGenerators")

package io.github.fomin.oasgen.gradle.typescript

import io.github.fomin.oasgen.gradle.oasGenVersion
import java.io.File

fun typescriptSimpleGenerator(
    outputDir: File,
    converterIds: List<String> = emptyList(),
) = TypeScriptGenerator(
    generatorId = "typescript-simple",
    generatorDependencies = listOf("io.github.fomin.oas-gen:oas-gen-typescript-simple-generator:$oasGenVersion"),
    outputDir = outputDir,
    converterIds = converterIds,
)
