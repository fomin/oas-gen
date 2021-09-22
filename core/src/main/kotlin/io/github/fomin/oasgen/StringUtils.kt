package io.github.fomin.oasgen

import java.lang.StringBuilder

fun String.indentWithMargin(indentLevel: Int) =
        this.lines().indentWithMargin(indentLevel)

fun Iterable<String>.indentWithMargin(indent: String): String {
    return this
            .flatMap { it.lines() }
            .joinToString("\n|$indent")
            .removeSuffix("\n|$indent")
            .lines()
            .joinToString("\n") { it.trimEnd() }
}

fun Iterable<String>.indentWithMargin(indentLevel: Int): String {
    val indent = (0 until indentLevel).fold(StringBuilder()) {sb, _ ->
        sb.append("    ")
        sb
    }.toString()
    return this.indentWithMargin(indent)
}

fun String.removeBlankLines(): String = this.lines().filter { it.isNotBlank() }.joinToString("\n")

fun String.trimEndings(): String = this.lines().map { it.trimEnd() }.joinToString("\n")

fun escapeReservedWordsAndChars(replacementChars: List<Pair<String, String>>, reservedWords: Set<String>, vararg parts: String): List<String> {
    val filteredParts = parts.map { part ->
        replacementChars.fold(part) { acc, replacementPair ->
            acc.replace(replacementPair.first, replacementPair.second)
        }
    }

    return when (filteredParts.size) {
        1 -> {
            val singlePart = filteredParts[0]
            when {
                reservedWords.contains(singlePart) -> listOf("$singlePart$")
                else -> listOf(singlePart)
            }
        }
        else -> return filteredParts
    }

}
