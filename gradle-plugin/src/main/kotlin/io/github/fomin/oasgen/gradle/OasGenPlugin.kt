package io.github.fomin.oasgen.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

sealed class GenerationSource

class DirectorySource(val dir: File) : GenerationSource()

class DependencySource(val dependency: String, val basePath: String) : GenerationSource() {
    constructor(dependency: String) : this(dependency, ".")
}

interface GeneratorSpec {
    fun configure(
        project: Project,
        name: String,
        generationSource: GenerationSource,
        schemaPath: String,
    ): TaskParameters

    fun onTaskCreated(
        project: Project,
        name: String,
        oasGenTaskProvider: TaskProvider<OasGenTask>,
    )
}

class TaskParameters(
    val generatorId: String,
    val converterIds: List<String>,
    val dtoNamespace: String,
    val routeNamespace: String,
    val dtoOutputDirProvider: Provider<Directory>,
    val routeOutputDirProvider: Provider<Directory>,
    val generatorDependencies: List<String>,
)

class SpecParameters(val name: String) {
    var source: GenerationSource? = null
    var schemaPath: String? = null
    var generator: GeneratorSpec? = null
}

abstract class OasGenExtension @Inject constructor(objectFactory: ObjectFactory) {
    internal val specParametersContainer: NamedDomainObjectContainer<SpecParameters>
    private val objectFactory: ObjectFactory

    init {
        this.objectFactory = objectFactory
        this.specParametersContainer = objectFactory.domainObjectContainer(SpecParameters::class.java)
    }

    fun generate(name: String, configurationAction: Action<SpecParameters>) {
        val specParameters = SpecParameters(name)
        configurationAction.execute(specParameters)
        specParametersContainer.add(specParameters)
    }

}

@Suppress("Unused")
class OasGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(BasePlugin::class.java)
        val oasGenExtension = project.extensions.create("oasGen", OasGenExtension::class.java)
        val aggregateTaskProvider = project.tasks.register("oasGen") {
            it.group = "generate code"
            it.description = "Aggregates other oasGen* tasks"
        }

        oasGenExtension.specParametersContainer.all { specParameters ->
            val name = specParameters.name
            val source = specParameters.source ?: error("Source is not set in generation $name")
            val schemaPath = specParameters.schemaPath ?: error("Schema path is not set in generation $name")
            val generatorSpec = specParameters.generator ?: error("Generator is not set in generation $name")
            val taskParameters = generatorSpec.configure(project, name, source, schemaPath)

            val suffix = name.capitalize()

            val generatorConfigurationName = "oasGenGenerator$suffix"
            val taskClasspath = project.configurations.register(generatorConfigurationName) { configuration ->
                configuration.isCanBeConsumed = false
                configuration.isCanBeResolved = true
            }
            taskParameters.generatorDependencies.forEach { generatorDependency ->
                project.dependencies.add(generatorConfigurationName, generatorDependency)
            }

            val sourceConfigurationName = "oasGenSource$suffix"
            val sourceConfigurationProvider =
                project.configurations.register(sourceConfigurationName) { configuration ->
                    configuration.isCanBeConsumed = false
                    configuration.isCanBeResolved = true
                }
            val sourceDependencyNotation: Any = when (source) {
                is DependencySource -> source.dependency
                is DirectorySource -> project.files(source.dir)
            }
            project.dependencies.add(sourceConfigurationName, sourceDependencyNotation)

            val basePath = when (source) {
                is DependencySource -> source.basePath
                is DirectorySource -> "."
            }

            val oasGenTaskProvider = project.tasks.register("oasGen$suffix", OasGenTask::class.java) { oasGenTask ->
                oasGenTask.group = "generate code"
                oasGenTask.description = "Generates code from the OpenAPI file"
                oasGenTask.generatorClasspathProvider.from(taskClasspath)
                oasGenTask.generatorId.set(taskParameters.generatorId)
                oasGenTask.sourceDependency.from(sourceConfigurationProvider)
                oasGenTask.basePathInSource.set(basePath)
                oasGenTask.schemaPath.set(schemaPath)
                oasGenTask.converterIds.set(taskParameters.converterIds)
                oasGenTask.dtoNamespace.set(taskParameters.dtoNamespace)
                oasGenTask.routeNamespace.set(taskParameters.routeNamespace)
                oasGenTask.dtoOutputDir.set(taskParameters.dtoOutputDirProvider)
                oasGenTask.routeOutputDir.set(taskParameters.routeOutputDirProvider)
            }

            generatorSpec.onTaskCreated(project, name, oasGenTaskProvider)

            aggregateTaskProvider.configure {
                it.dependsOn(oasGenTaskProvider)
            }
        }

    }
}
