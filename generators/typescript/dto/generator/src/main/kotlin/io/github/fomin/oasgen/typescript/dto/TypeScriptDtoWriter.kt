package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.Fragment
import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.indentWithMargin

class TypeScriptDtoWriter {
    data class Result(
            val content: String,
            val importDeclarations: List<ImportDeclaration>
    )

    fun write(
            typeConverterRegistry: TypeConverterRegistry,
            jsonSchemas: List<JsonSchema>
    ): Result {
        val schemaQueue = jsonSchemas.toMutableList()
        val processedTypes = mutableSetOf<String>()
        val contentList = mutableListOf<String>()
        val importDeclarations = mutableListOf<ImportDeclaration>()
        var index = 0

        while (index < schemaQueue.size) {
            val jsonSchema = schemaQueue[index]
            val typeConverter = typeConverterRegistry[jsonSchema]
            if (!processedTypes.contains(typeConverter.type())) {
                typeConverter.content()?.let { contentList.add(it) }
                importDeclarations.addAll(typeConverter.importDeclarations())
                schemaQueue.addAll(typeConverter.innerSchemas())
                val jsonConverter = typeConverter.jsonConverter
                if (jsonConverter != null) {
                    val content = jsonConverter.content()
                    if (content != null) contentList.add(content)
                }
            }
            processedTypes.add(typeConverter.type())
            index += 1
        }
        return Result(contentList.joinToString("\n\n").indentWithMargin(0), importDeclarations)
    }
}
