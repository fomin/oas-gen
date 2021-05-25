package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.OutputFile
import io.github.fomin.oasgen.Writer

class JavaDtoWriter(
        private val converterRegistry: ConverterRegistry
) : Writer<JsonSchema> {
    override fun write(items: Iterable<JsonSchema>): List<OutputFile> {

        val schemaQueue = items.toMutableList()
        val processedSchemas = mutableSetOf<JsonSchema>()
        val outputFiles = mutableSetOf<OutputFile>()
        var index = 0

        while (index < schemaQueue.size) {
            val jsonSchema = schemaQueue[index]
            if (!processedSchemas.contains(jsonSchema)) {
                val converterWriter = converterRegistry[jsonSchema]
                val (schemaOutputFiles, innerSchemas) = converterWriter.generate()
                processedSchemas.add(jsonSchema)
                outputFiles.addAll(schemaOutputFiles)
                schemaQueue.addAll(innerSchemas)
            }
            index += 1
        }
        return outputFiles.toList()
    }

}
