package io.github.fomin.oasgen

import org.apache.commons.cli.*
import java.io.File
import kotlin.system.exitProcess

const val BASE_DIR = "base-dir"
const val PATH = "path"
const val SCHEMA = "schema"
const val OUTPUT_DIR = "output-dir"
const val NAMESPACE = "namespace"
const val GENERATOR = "generator"
const val CONVERTERS = "converters"

fun main(args: Array<String>) {
    val options = Options()
            .addOption("b", BASE_DIR, true, "base directory")
            .addOption("p", PATH, true, "schema path (relative to base directory)")
            .addOption("s", SCHEMA, true, "schema file")
            .addRequiredOption("o", OUTPUT_DIR, true, "output directory")
            .addRequiredOption("n", NAMESPACE, true, "namespace")
            .addRequiredOption("g", GENERATOR, true, "generator identifier")
            .addOption(
                    Option.builder("c")
                            .longOpt(CONVERTERS)
                            .hasArgs()
                            .desc("converter identifiers")
                            .build()
            )

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
    val converterIdsArray = commandLine.getOptionValues(CONVERTERS)
    val converterIds = converterIdsArray?.toList() ?: emptyList<String>()

    val (baseDir, schemaPath) = if (baseDirArg == null) {
        val schemaFile = File(schemaFileArg)
        Pair(schemaFile.parentFile, schemaFile.name)
    } else {
        Pair(File(baseDirArg), schemaPathArg)
    }

    val outputDir = File(outputDirArg)
    openApiGenerate(generatorId, baseDir, outputDir,outputDir, schemaPath, namespaceArg, namespaceArg, converterIds)
}
