package io.github.fomin.oasgen

import io.github.fomin.oasgen.java.rest.operations.JavaSpringRestOperationsWriter
import io.github.fomin.oasgen.java.spring.mvc.JavaSrpingMvcServerWriter
import io.github.fomin.oasgen.typescript.axios.AxiosClientWriter
import org.apache.commons.cli.*
import java.io.File
import kotlin.system.exitProcess

const val BASE_DIR = "base-dir"
const val PATH = "path"
const val SCHEMA = "schema"
const val OUTPUT_DIR = "output-dir"
const val NAMESPACE = "namespace"
const val GENERATOR = "generator"

private val writerFactories = mapOf(
        "java-spring-mvc" to ::JavaSrpingMvcServerWriter,
        "java-spring-rest-operations" to ::JavaSpringRestOperationsWriter,
        "typescript-axios" to ::AxiosClientWriter
)

fun main(args: Array<String>) {
    val options = Options()
            .addOption("b", BASE_DIR, true, "base directory")
            .addOption("p", PATH, true, "schema path (relative to base directory)")
            .addOption("s", SCHEMA, true, "schema file")
            .addRequiredOption("o", OUTPUT_DIR, true, "output directory")
            .addRequiredOption("n", NAMESPACE, true, "namespace")
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
    val schemaPathArg = commandLine.getOptionValue(PATH)
    val schemaFileArg = commandLine.getOptionValue(SCHEMA)
    val outputDirArg = commandLine.getOptionValue(OUTPUT_DIR)
    val namespaceArg = commandLine.getOptionValue(NAMESPACE)
    val generatorId = commandLine.getOptionValue(GENERATOR)

    val (baseDir, schemaPath) = if (baseDirArg == null) {
        val schemaFile = File(schemaFileArg)
        Pair(schemaFile.parentFile, schemaFile.name)
    } else {
        Pair(File(baseDirArg), schemaPathArg)
    }
    val fragmentRegistry = FragmentRegistry(baseDir)
    val openApiSchema = OpenApiSchema(fragmentRegistry.get(Reference.root(schemaPath)), null)

    val writerFactory = writerFactories[generatorId] ?: error("Can't find generator $generatorId")
    val writer = writerFactory(namespaceArg)

    val outputFiles = writer.write(listOf(openApiSchema))
    val outputDir = File(outputDirArg)
    outputDir.mkdirs()
    outputFiles.forEach { outputFile ->
        val generatedFile = File(outputDir, outputFile.path)
        generatedFile.parentFile.mkdirs()
        generatedFile.writeText(outputFile.content)
    }

}
