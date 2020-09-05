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
        val baseDir: File,
        val outputDir: File?,
        val schemaPath: String,
        val namespace: String,
        val converterIds: Array<out String>,
        val javaSources: Boolean
) : Serializable

open class OasGenExtension {
    val generationSpecs: MutableList<GenerationSpec> = mutableListOf()

    @Suppress("Unused")
    fun generate(
            generatorId: String,
            baseDir: File,
            outputDir: File? = null,
            schemaPath: String,
            namespace: String,
            vararg converterIds: String = emptyArray(),
            javaSources: Boolean = false
    ) {
        generationSpecs.add(GenerationSpec(generatorId, baseDir, outputDir, schemaPath, namespace, converterIds, javaSources))
    }
}

interface GenerationWorkParameters : WorkParameters {
    val buildDir: Property<File>
    val generationSpecs: ListProperty<GenerationSpec>
}

abstract class OasGenAction : WorkAction<GenerationWorkParameters> {
    override fun execute() {
        parameters.generationSpecs.get().forEachIndexed { index, generationSpec ->
            openApiGenerate(
                    generationSpec.generatorId,
                    generationSpec.baseDir,
                    effectiveOutputDir(parameters.buildDir.get(), index, generationSpec),
                    generationSpec.schemaPath,
                    generationSpec.namespace,
                    generationSpec.converterIds.asList()
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
        workQueue.submit(OasGenAction::class.java) { t ->
            t.buildDir.set(project.buildDir)
            t.generationSpecs.set(oasGenExtension.generationSpecs)
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
            configuration.defaultDependencies { dependencySet ->
                val version = this.javaClass.classLoader.getResource("META-INF/oas-gen/version.txt")!!
                        .readText().trim()
                val dependency = project.dependencies.create("io.github.fomin.oas-gen:oas-gen-core:$version")
                dependencySet.add(dependency)
            }
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
        }

        project.tasks.register("oasGen", OasGenTask::class.java, oasGenExtension, oasGenConfiguration).configure { task ->
            oasGenExtension.generationSpecs.forEachIndexed { index, generationSpec ->
                task.inputs.property("generatorId$index", generationSpec.generatorId)
                task.inputs.property("schemaPath$index", generationSpec.schemaPath)
                task.inputs.property("namespace$index", generationSpec.namespace)
                task.inputs.property("converterIds$index", generationSpec.converterIds.joinToString())
                task.inputs.dir(generationSpec.baseDir).withPathSensitivity(PathSensitivity.RELATIVE)
                task.outputs.dir(effectiveOutputDir(project.buildDir, index, generationSpec))
            }
        }
    }
}

private fun effectiveOutputDir(buildDir: File, index: Int, generationSpec: GenerationSpec) =
        generationSpec.outputDir ?: File(buildDir, "oas-gen/generated${index}")
