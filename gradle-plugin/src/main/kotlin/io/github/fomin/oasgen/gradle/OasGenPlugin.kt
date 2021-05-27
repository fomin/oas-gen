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
    val dtoOutputDir: File?,
    val routeOutputDir: File?,
    val schemaPath: String,
    val dtoNamespace: String,
    val routeNamespace: String,
    val converterIds: Array<out String>,
    val javaSources: Boolean,
    val baseClass: String?,
    val baseInterface: String?
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
            javaSources: Boolean = false,
            dtoBaseClass: String? = null,
            dtoBaseInterface: String? = null
    ) {
        generationSpecs.add(GenerationSpec(generatorId, DirectoryGenerationSource(baseDir), outputDir, outputDir, schemaPath, namespace, namespace, converterIds, javaSources, dtoBaseClass, dtoBaseInterface))
    }

    @Suppress("Unused")
    fun generateFromDirectory(
        generatorId: String,
        baseDir: File,
        dtoOutputDir: File? = null,
        routeOutputDir: File? = null,
        schemaPath: String,
        dtoNamespace: String,
        routeNamespace: String,
        vararg converterIds: String = emptyArray(),
        javaSources: Boolean = false,
        dtoBaseClass: String? = null,
        dtoBaseInterface: String? = null
    ) {
        generationSpecs.add(GenerationSpec(generatorId, DirectoryGenerationSource(baseDir), dtoOutputDir, routeOutputDir, schemaPath, dtoNamespace, routeNamespace, converterIds, javaSources, dtoBaseClass, dtoBaseInterface))
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
            javaSources: Boolean = false,
            dtoBaseClass: String? = null,
            dtoBaseInterface: String? = null
    ) {
        generationSpecs.add(GenerationSpec(generatorId, DependencyGenerationSource(dependency, basePath), outputDir, outputDir, schemaPath, namespace, namespace, converterIds, javaSources, dtoBaseClass, dtoBaseInterface))
    }

    @Suppress("Unused")
    fun generateFromDependency(
        generatorId: String,
        dependency: String,
        basePath: String = ".",
        dtoOutputDir: File? = null,
        routeOutputDir: File? = null,
        schemaPath: String,
        dtoNamespace: String,
        routeNamespace: String,
        vararg converterIds: String = emptyArray(),
        javaSources: Boolean = false,
        dtoBaseClass: String? = null,
        dtoBaseInterface: String? = null
    ) {
        generationSpecs.add(GenerationSpec(generatorId, DependencyGenerationSource(dependency, basePath), dtoOutputDir, routeOutputDir, schemaPath, dtoNamespace, routeNamespace, converterIds, javaSources, dtoBaseClass, dtoBaseInterface))
    }
}

interface GenerationWorkParameters : WorkParameters {
    val items: ListProperty<OasGenActionParameters>
    val buildDir: Property<File>
}

class OasGenActionParameters(
    val generatorId: String,
    val baseDir: File,
    val dtoOutputDir: File,
    val routeOutputDir: File,
    val schemaPath: String,
    val dtoNamespace: String,
    val routeNamespace: String,
    val dtoBaseClass: String?,
    val dtoBaseInterface: String?,
    val converterIds: Array<out String>
) : Serializable

abstract class OasGenAction : WorkAction<GenerationWorkParameters> {
    override fun execute() {
        parameters.items.get().forEach { item ->
            openApiGenerate(
                    item.generatorId,
                    item.baseDir,
                    item.dtoOutputDir,
                    item.routeOutputDir,
                    item.schemaPath,
                    item.dtoNamespace,
                    item.routeNamespace,
                    item.dtoBaseClass,
                    item.dtoBaseInterface,
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
                val dtoOutputDir = effectiveDtoOutputDir(project.buildDir, index, generationSpec)
                if (dtoOutputDir.exists()) {
                    dtoOutputDir.deleteRecursively()
                }
                val routeOutputDir = effectiveRouteOutputDir(project.buildDir, index, generationSpec)
                if (routeOutputDir.exists()) {
                    routeOutputDir.deleteRecursively()
                }
                OasGenActionParameters(generationSpec.generatorId,
                    baseDir,
                    dtoOutputDir,
                    routeOutputDir,
                    generationSpec.schemaPath,
                    generationSpec.dtoNamespace,
                    generationSpec.routeNamespace,
                    generationSpec.baseClass,
                    generationSpec.baseInterface,
                    generationSpec.converterIds)
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
                task.inputs.property("dtoNamespace$index", generationSpec.dtoNamespace)
                task.inputs.property("routeNamespace$index", generationSpec.routeNamespace)
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
                if (generationSpec.routeOutputDir != null && generationSpec.dtoOutputDir != generationSpec.routeOutputDir) {
                    task.outputs.dirs(effectiveDtoOutputDir(project.buildDir, index, generationSpec),
                        effectiveRouteOutputDir(project.buildDir, index, generationSpec))
                } else {
                    task.outputs.dir(effectiveDtoOutputDir(project.buildDir, index, generationSpec))
                }
            }
            task.dependsOn(oasGenConfiguration)
        }

        project.afterEvaluate {
            oasGenExtension.generationSpecs.forEachIndexed { index, generationSpec ->
                if (generationSpec.javaSources) {
                    val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
                    javaConvention.sourceSets.getAt(SourceSet.MAIN_SOURCE_SET_NAME).java {
                        if (generationSpec.routeOutputDir != null && generationSpec.dtoOutputDir != generationSpec.routeOutputDir) {
                            it.srcDirs(effectiveDtoOutputDir(project.buildDir, index, generationSpec),
                                effectiveRouteOutputDir(project.buildDir, index, generationSpec))
                        } else {
                            it.srcDir(effectiveDtoOutputDir(project.buildDir, index, generationSpec))
                        }
                    }
                }
            }

            if (oasGenExtension.generationSpecs.any { it.javaSources }) {
                project.tasks.getAt("compileJava").dependsOn(oasGenTask)
            }
        }
    }
}

private fun effectiveDtoOutputDir(buildDir: File, index: Int, generationSpec: GenerationSpec) =
        generationSpec.dtoOutputDir ?: File(buildDir, "oas-gen/generated${index}")

private fun effectiveRouteOutputDir(buildDir: File, index: Int, generationSpec: GenerationSpec) =
    generationSpec.routeOutputDir ?: File(buildDir, "oas-gen/generated${index}")
