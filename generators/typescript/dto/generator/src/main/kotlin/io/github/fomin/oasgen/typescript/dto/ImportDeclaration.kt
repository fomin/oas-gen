package io.github.fomin.oasgen.typescript.dto

data class ImportDeclaration(
        val type: String,
        val module: String
) {
    companion object {
        fun toString(importDeclarations: List<ImportDeclaration>): String = importDeclarations.groupBy {
                    it.module
                }.map { (module, moduleImportDeclarations) ->
                    val sortedTypes = moduleImportDeclarations.map { it.type }.toSortedSet()
                    """import {${sortedTypes.joinToString()}} from "$module";"""
                }
                .sorted()
                .joinToString("\n")
    }
}
