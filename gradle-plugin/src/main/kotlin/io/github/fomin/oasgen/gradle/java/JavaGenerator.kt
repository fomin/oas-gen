package io.github.fomin.oasgen.gradle.java

import io.github.fomin.oasgen.gradle.*
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import java.io.File

sealed class OutputConfiguration

class SingleOutput(
    val outputDir: File?,
    val sourceSetProvider: NamedDomainObjectProvider<SourceSet>?
) : OutputConfiguration() {
    constructor(sourceSetProvider: NamedDomainObjectProvider<SourceSet>?) : this(null, sourceSetProvider)
    constructor() : this(null, null)
}

class SeparatedOutput(
    val dto: SingleOutput?,
    val route: SingleOutput?,
) : OutputConfiguration()

private sealed class EffectiveOutputConfiguration

private class EffectiveSingleOutput(
    val outputDir: File,
    val sourceSetProvider: NamedDomainObjectProvider<SourceSet>?,
) : EffectiveOutputConfiguration()

private class EffectiveSeparatedOutput(
    val dto: EffectiveSingleOutput,
    val route: EffectiveSingleOutput,
) : EffectiveOutputConfiguration()

sealed class NamespaceConfiguration

class SingleNamespace(val namespace: String) : NamespaceConfiguration()

class SeparatedNamespace(val dtoNamespace: String, val routeNamespace: String) : NamespaceConfiguration()

class JavaGenerator(
    private val generatorId: String,
    private val generatorDependencies: List<String>,
    private val namespaceConfiguration: NamespaceConfiguration,
    private val outputConfiguration: OutputConfiguration,
    private val converterIds: List<String> = emptyList(),
    private val apiDependencies: List<String> = emptyList(),
) : GeneratorSpec {

    private fun effectiveOutputConfiguration(project: Project, name: String): EffectiveOutputConfiguration {
        val defaultOutputDir = File(project.buildDir, "oas-gen/${name}")
        return when (outputConfiguration) {
            is SingleOutput -> EffectiveSingleOutput(
                outputConfiguration.outputDir ?: defaultOutputDir,
                outputConfiguration.sourceSetProvider,
            )
            is SeparatedOutput -> EffectiveSeparatedOutput(
                EffectiveSingleOutput(
                    outputConfiguration.dto?.outputDir ?: defaultOutputDir,
                    outputConfiguration.dto?.sourceSetProvider
                ),
                EffectiveSingleOutput(
                    outputConfiguration.route?.outputDir ?: defaultOutputDir,
                    outputConfiguration.route?.sourceSetProvider
                ),
            )
        }
    }

    override fun configure(
        project: Project,
        name: String,
        generationSource: GenerationSource,
        schemaPath: String,
    ): TaskParameters {

        val effectiveOutputConfiguration = effectiveOutputConfiguration(project, name)

        val dtoOutputDirNameProvider = when (effectiveOutputConfiguration) {
            is EffectiveSingleOutput -> effectiveOutputConfiguration.outputDir.path
            is EffectiveSeparatedOutput -> effectiveOutputConfiguration.dto.outputDir.path
        }
        val initialDtoOutputDirProvider = project.objects.directoryProperty()
        initialDtoOutputDirProvider.set(File("."))
        val dtoOutputDirProvider = initialDtoOutputDirProvider.dir(dtoOutputDirNameProvider)

        val routeOutputDirNameProvider = when (effectiveOutputConfiguration) {
            is EffectiveSingleOutput -> effectiveOutputConfiguration.outputDir.path
            is EffectiveSeparatedOutput -> effectiveOutputConfiguration.route.outputDir.path
        }
        val initialRouteOutputDirProvider = project.objects.directoryProperty()
        initialRouteOutputDirProvider.set(File("."))
        val routeOutputDirProvider = initialRouteOutputDirProvider.dir(routeOutputDirNameProvider)

        val dtoNamespace = when (namespaceConfiguration) {
            is SingleNamespace -> namespaceConfiguration.namespace
            is SeparatedNamespace -> namespaceConfiguration.dtoNamespace
        }

        val routeNamespace = when (namespaceConfiguration) {
            is SingleNamespace -> namespaceConfiguration.namespace
            is SeparatedNamespace -> namespaceConfiguration.routeNamespace
        }

        return TaskParameters(
            generatorId,
            converterIds,
            dtoNamespace,
            routeNamespace,
            dtoOutputDirProvider,
            routeOutputDirProvider,
            generatorDependencies,
        )
    }

    override fun onTaskCreated(
        project: Project,
        name: String,
        oasGenTaskProvider: TaskProvider<OasGenTask>,
    ) {
        when (val effectiveOutputConfiguration = effectiveOutputConfiguration(project, name)) {
            is EffectiveSingleOutput -> {
                addSourceSet(oasGenTaskProvider, project, effectiveOutputConfiguration)
            }
            is EffectiveSeparatedOutput -> {
                addSourceSet(oasGenTaskProvider, project, effectiveOutputConfiguration.dto)
                addSourceSet(oasGenTaskProvider, project, effectiveOutputConfiguration.route)
            }
        }
    }

    private fun addSourceSet(
        oasGenTaskProvider: TaskProvider<OasGenTask>,
        project: Project,
        outputConfiguration: EffectiveSingleOutput,
    ) {
        outputConfiguration.sourceSetProvider?.configure { sourceSet ->
            sourceSet.java {
                it.srcDir(outputConfiguration.outputDir)
            }
            project.tasks.named(sourceSet.compileJavaTaskName) {
                it.dependsOn(oasGenTaskProvider)
            }
            apiDependencies.forEach {
                val configurationName = if (project.configurations.findByName(sourceSet.apiConfigurationName) != null) {
                    sourceSet.apiConfigurationName
                } else {
                    sourceSet.implementationConfigurationName
                }
                project.dependencies.add(configurationName, it)
            }
        }
    }
}
