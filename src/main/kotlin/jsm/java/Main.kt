package jsm.java

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import jsm.FragmentRegistry
import jsm.OpenApiSchema
import jsm.Reference
import jsm.RootFragment
import jsm.java.rest.operations.JavaSpringRestOperationsWriter
import jsm.java.spring.mvc.JavaSrpingMvcServerWriter
import jsm.typescript.axios.AxiosClientWriter
import org.apache.commons.cli.*
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

const val BASE_DIR = "base-dir"
const val SCHEMA = "schema"
const val COMPONENTS = "components"
const val OUTPUT_DIR = "output-dir"
const val PACKAGE = "package"
const val GENERATOR = "generator"

private val writerFactories = mapOf(
        "java-spring-mvc" to ::JavaSrpingMvcServerWriter,
        "java-spring-rest-operations" to ::JavaSpringRestOperationsWriter,
        "typescript-axios" to ::AxiosClientWriter
)

fun main(args: Array<String>) {
    val options = Options()
            .addRequiredOption("b", BASE_DIR, true, "base directory")
            .addRequiredOption("s", SCHEMA, true, "schema file")
            .addOption(
                    Option.builder("c").longOpt(COMPONENTS)
                            .hasArgs().desc("component files").build()
            )
            .addRequiredOption("o", OUTPUT_DIR, true, "output directory")
            .addRequiredOption("p", PACKAGE, true, "package name")
            .addRequiredOption("g", GENERATOR, true, "generator identifier")

    val parser = DefaultParser()
    val commandLine: CommandLine
    try {
        commandLine = parser.parse(options, args)
    } catch (exp: ParseException) {
        println(exp.message)
        val formatter = HelpFormatter()
        formatter.printHelp("java", options)
        exitProcess(1)
    }

    val baseDirArg = commandLine.getOptionValue(BASE_DIR)
    val schemaFileArg = commandLine.getOptionValue(SCHEMA)
    val componentsArg = commandLine.getOptionValues(COMPONENTS) ?: emptyArray()
    val outputDirArg = commandLine.getOptionValue(OUTPUT_DIR)
    val packageName = commandLine.getOptionValue(PACKAGE)
    val generatorId = commandLine.getOptionValue(GENERATOR)

    val yamlMapper = ObjectMapper(YAMLFactory())
    val jsonMapper = ObjectMapper()
    val mapTypeReference = object : TypeReference<Map<*, *>>() {}
    val basePath = Paths.get(baseDirArg)

    fun loadMap(filePathStr: String): RootFragment {
        val mapper = when (val extension = filePathStr.substring(filePathStr.lastIndexOf(".") + 1)) {
            "json" -> jsonMapper
            "yaml" -> yamlMapper
            "yml" -> yamlMapper
            else -> error("Unsupported extension $extension")
        }
        val map = mapper.readValue(File(filePathStr), mapTypeReference)
        val filePath = Paths.get(filePathStr)
        val relativeFilePath = basePath.relativize(filePath)
        return RootFragment(relativeFilePath.toString(), map)
    }

    val schemaRootFragment = loadMap(schemaFileArg)

    val componentFragments = componentsArg.map { componentArg ->
        loadMap(componentArg)
    }

    val fragmentRegistry = FragmentRegistry(componentFragments + schemaRootFragment)
    val openApiSchema = OpenApiSchema(fragmentRegistry.get(Reference.root(schemaRootFragment.path)), null)

    val writerFactory = writerFactories[generatorId] ?: error("Can't find generator $generatorId")
    val writer = writerFactory(packageName)

    val outputFiles = writer.write(listOf(openApiSchema))
    val outputDir = File(outputDirArg)
    outputDir.mkdirs()
    outputFiles.forEach { outputFile ->
        val generatedFile = File(outputDir, outputFile.path)
        generatedFile.parentFile.mkdirs()
        generatedFile.writeText(outputFile.content)
    }

}
