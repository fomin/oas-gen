package jsm

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
