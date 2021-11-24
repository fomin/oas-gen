package io.github.fomin.oasgen.gradle.typescript

import io.github.fomin.oasgen.gradle.GenerationSource
import io.github.fomin.oasgen.gradle.GeneratorSpec
import io.github.fomin.oasgen.gradle.OasGenTask
import io.github.fomin.oasgen.gradle.TaskParameters
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File

class TypeScriptGenerator(
    private val generatorId: String,
    private val generatorDependencies: List<String>,
    private val outputDir: File,
    private val converterIds: List<String> = emptyList(),
) : GeneratorSpec {
    override fun configure(
        project: Project,
        name: String,
        generationSource: GenerationSource,
        schemaPath: String
    ): TaskParameters {
        val outputDirProvider = project.objects.directoryProperty()
        outputDirProvider.set(outputDir)

        return TaskParameters(
            generatorId,
            converterIds,
            "dummy",
            "dummy",
            outputDirProvider,
            outputDirProvider,
            generatorDependencies,
        )
    }

    override fun onTaskCreated(project: Project, name: String, oasGenTaskProvider: TaskProvider<OasGenTask>) {
        // do nothing
    }
}
