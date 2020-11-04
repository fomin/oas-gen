package io.github.fomin.oasgen.typescript

import io.github.fomin.oasgen.JsonSchema
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

fun jsDoc(jsonSchema: JsonSchema): String {
    val jsDocContent = listOfNotNull(jsonSchema.title?.trim(), jsonSchema.description?.trim())
        .joinToString("\n\n")
        .lines()
        .joinToString("\n") { " * $it".trimEnd() }

    return """|/**
              |$jsDocContent
              | */""".trimMargin()
}
