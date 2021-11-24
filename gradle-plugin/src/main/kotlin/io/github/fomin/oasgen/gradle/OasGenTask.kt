package io.github.fomin.oasgen.gradle

import io.github.fomin.oasgen.openApiGenerate
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

private interface OasGenActionParameters : WorkParameters {
    val generatorId: Property<String>
    val baseDir: DirectoryProperty
    val schemaPath: Property<String>
    val dtoNamespace: Property<String>
    val routeNamespace: Property<String>
    val converterIds: ListProperty<String>
    val dtoOutputDir: DirectoryProperty
    val routeOutputDir: DirectoryProperty
}

private abstract class OasGenAction : WorkAction<OasGenActionParameters> {
    override fun execute() {
        openApiGenerate(
            parameters.generatorId.get(),
            parameters.baseDir.asFile.get(),
            parameters.dtoOutputDir.asFile.get(),
            parameters.routeOutputDir.asFile.get(),
            parameters.schemaPath.get(),
            parameters.dtoNamespace.get(),
            parameters.routeNamespace.get(),
            parameters.converterIds.get()
        )
    }
}

@CacheableTask
abstract class OasGenTask @Inject constructor(
) : DefaultTask() {

    @get:Input
    abstract val generatorId: Property<String>

    @get:Classpath
    abstract val sourceDependency: ConfigurableFileCollection

    @get:Input
    @get:Optional
    abstract val basePathInSource: Property<String>

    @get:Input
    abstract val schemaPath: Property<String>

    @get:Input
    abstract val dtoNamespace: Property<String>

    @get:Input
    abstract val routeNamespace: Property<String>

    @get:Input
    abstract val converterIds: ListProperty<String>

    @get:Classpath
    abstract val generatorClasspathProvider: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val dtoOutputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val routeOutputDir: DirectoryProperty

    @get:Inject
    open val workerExecutor: WorkerExecutor
        get() {
            // Getter body is ignored
            throw UnsupportedOperationException()
        }

    @TaskAction
    fun execute() {
        val workQueue = workerExecutor.classLoaderIsolation { workerSpec ->
            workerSpec.classpath.from(generatorClasspathProvider)
        }

        val sourceFile = sourceDependency.singleFile
        val baseDir =
            if (sourceFile.isDirectory) {
                sourceFile
            } else {
                val unzipDir = "${project.buildDir}/oas-gen/${this.name}/schema"
                project.copy {
                    it.from(project.zipTree(sourceFile))
                    it.into(unzipDir)
                }
                project.file(unzipDir)
            }
        val pathInSourceValue = basePathInSource.getOrElse(".")
        val schemaDir = baseDir.resolve(pathInSourceValue)

        workQueue.submit(OasGenAction::class.java) { actionParameters ->
            actionParameters.generatorId.set(generatorId)
            actionParameters.baseDir.set(schemaDir)
            actionParameters.schemaPath.set(schemaPath)
            actionParameters.dtoNamespace.set(dtoNamespace)
            actionParameters.routeNamespace.set(routeNamespace)
            actionParameters.converterIds.set(converterIds)
            actionParameters.dtoOutputDir.set(dtoOutputDir)
            actionParameters.routeOutputDir.set(routeOutputDir)
        }
    }
}
