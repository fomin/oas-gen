package io.github.fomin.oasgen.java

import io.github.fomin.oasgen.JsonSchema
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

private val parser = Parser.builder().build()
private val renderer = HtmlRenderer.builder().build()

fun javaDoc(jsonSchema: JsonSchema): String {
    val renderedDescription = jsonSchema.description?.let { description ->
        val document = parser.parse(description)
        renderer.render(document).trim()
    }
    val titleAndDescription = listOfNotNull(jsonSchema.title?.trim(), renderedDescription)
    if (titleAndDescription.isNotEmpty()) {
        val javaDocContent = titleAndDescription
            .joinToString("\n\n")
            .lines()
            .joinToString("\n") { " * $it".trimEnd() }

        return """|/**
              |$javaDocContent
              | */""".trimMargin()
    } else {
        return ""
    }
}
