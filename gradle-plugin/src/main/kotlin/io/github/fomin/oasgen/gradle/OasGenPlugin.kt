@file:Suppress("UnstableApiUsage")

package io.github.fomin.oasgen.gradle

import io.github.fomin.oasgen.openApiGenerate
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.io.Serializable
import javax.inject.Inject

class GenerationSpec(
        val generatorId: String,
        val generationSource: GenerationSource,
        val outputDir: File?,
        val schemaPath: String,
        val namespace: String,
        val converterIds: Array<out String>,
        val javaSources: Boolean
)

sealed class GenerationSource

class DirectoryGenerationSource(
        val baseDir: File
) : GenerationSource()

class DependencyGenerationSource(
        val dependency: String,
        val basePath: String
) : GenerationSource()

open class OasGenExtension {
    internal val generationSpecs: MutableList<GenerationSpec> = mutableListOf()

    @Suppress("Unused")
    fun generateFromDirectory(
            generatorId: String,
            baseDir: File,
            outputDir: File? = null,
            schemaPath: String,
            namespace: String,
            vararg converterIds: String = emptyArray(),
            javaSources: Boolean = false
    ) {
        generationSpecs.add(GenerationSpec(generatorId, DirectoryGenerationSource(baseDir), outputDir, schemaPath, namespace, converterIds, javaSources))
    }

    @Suppress("Unused")
    fun generateFromDependency(
            generatorId: String,
            dependency: String,
            basePath: String = ".",
            outputDir: File? = null,
            schemaPath: String,
            namespace: String,
            vararg converterIds: String = emptyArray(),
            javaSources: Boolean = false
    ) {
        generationSpecs.add(GenerationSpec(generatorId, DependencyGenerationSource(dependency, basePath), outputDir, schemaPath, namespace, converterIds, javaSources))
    }
}

interface GenerationWorkParameters : WorkParameters {
    val items: ListProperty<OasGenActionParameters>
    val buildDir: Property<File>
}

class OasGenActionParameters(
        val generatorId: String,
        val baseDir: File,
        val outputDir: File,
        val schemaPath: String,
        val namespace: String,
        val converterIds: Array<out String>
) : Serializable

abstract class OasGenAction : WorkAction<GenerationWorkParameters> {
    override fun execute() {
        parameters.items.get().forEach { item ->
            openApiGenerate(
                    item.generatorId,
                    item.baseDir,
                    item.outputDir,
                    item.schemaPath,
                    item.namespace,
                    item.converterIds.asList()
            )
        }
    }
}

@CacheableTask
open class OasGenTask @Inject constructor(
        private val oasGenExtension: OasGenExtension,
        private val generatorClasspathProvider: Provider<Configuration>
) : DefaultTask() {

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
        workQueue.submit(OasGenAction::class.java) { oasGenActionParametersList ->
            oasGenActionParametersList.buildDir.set(project.buildDir)
            oasGenActionParametersList.items.set(oasGenExtension.generationSpecs.mapIndexed { index, generationSpec ->
                val baseDir = when (val generationSource = generationSpec.generationSource) {
                    is DirectoryGenerationSource -> {
                        generationSource.baseDir
                    }
                    is DependencyGenerationSource -> {
                        val configuration = project.configurations.register("oasGen$index") { configuration ->
                            configuration.isCanBeResolved = true
                            configuration.isCanBeConsumed = false
                            configuration.defaultDependencies { dependencySet ->
                                val dependency = project.dependencies.create(generationSource.dependency)
                                dependencySet.add(dependency)
                            }
                        }
                        val zipFile = configuration.get().files.single()
                        val unzipDir = "${project.buildDir}/oas-gen/unzipped$index"
                        project.copy {
                            it.from(project.zipTree(zipFile))
                            it.into(unzipDir)
                        }
                        File(unzipDir).resolve(generationSource.basePath).absoluteFile
                    }
                }
                val outputDir = effectiveOutputDir(project.buildDir, index, generationSpec)
                if (outputDir.exists()) {
                    outputDir.deleteRecursively()
                }
                OasGenActionParameters(generationSpec.generatorId, baseDir, outputDir, generationSpec.schemaPath, generationSpec.namespace, generationSpec.converterIds)
            })
        }
    }
}

@Suppress("Unused")
class OasGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(BasePlugin::class.java)

        val oasGenExtension = project.extensions.create("oasGen", OasGenExtension::class.java)
        val oasGenConfiguration = project.configurations.register("oasGen") { configuration ->
            configuration.isCanBeResolved = true
            configuration.isCanBeConsumed = false
        }

        val oasGenTask = project.tasks.register("oasGen", OasGenTask::class.java, oasGenExtension, oasGenConfiguration)
        oasGenTask.configure { task ->
            oasGenExtension.generationSpecs.forEachIndexed { index, generationSpec ->
                task.inputs.property("generatorId$index", generationSpec.generatorId)
                task.inputs.property("schemaPath$index", generationSpec.schemaPath)
                task.inputs.property("namespace$index", generationSpec.namespace)
                task.inputs.property("converterIds$index", generationSpec.converterIds.joinToString())
                when (val generationSource = generationSpec.generationSource) {
                    is DirectoryGenerationSource -> {
                        task.inputs.dir(generationSource.baseDir).withPathSensitivity(PathSensitivity.RELATIVE)
                    }
                    is DependencyGenerationSource -> {
                        task.inputs.property("dependency$index", generationSource.dependency)
                        task.inputs.property("basePath$index", generationSource.basePath)
                    }
                }
                task.outputs.dir(effectiveOutputDir(project.buildDir, index, generationSpec))
            }
            task.dependsOn(oasGenConfiguration)
        }

        project.afterEvaluate {
            oasGenExtension.generationSpecs.forEachIndexed { index, generationSpec ->
                if (generationSpec.javaSources) {
                    val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
                    javaConvention.sourceSets.getAt(SourceSet.MAIN_SOURCE_SET_NAME).java {
                        it.srcDir(effectiveOutputDir(project.buildDir, index, generationSpec))
                    }
                }
            }

            if (oasGenExtension.generationSpecs.any { it.javaSources }) {
                project.tasks.getAt("compileJava").dependsOn(oasGenTask)
            }
        }
    }
}

private fun effectiveOutputDir(buildDir: File, index: Int, generationSpec: GenerationSpec) =
        generationSpec.outputDir ?: File(buildDir, "oas-gen/generated${index}")
