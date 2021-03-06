package io.github.fomin.oasgen.typescript

import io.github.fomin.oasgen.escapeReservedWordsAndChars
import io.github.fomin.oasgen.java.toLowerCamelCase

private val replacementChars = listOf(
        "@" to "At-",
        "/" to "Slash-"
)

private val reservedWords = setOf(
        "break",
        "case",
        "catch",
        "class",
        "const",
        "continue",
        "debugger",
        "default",
        "delete",
        "do",
        "else",
        "enum",
        "export",
        "extends",
        "false",
        "finally",
        "for",
        "function",
        "if",
        "import",
        "in",
        "instanceof",
        "new",
        "null",
        "return",
        "super",
        "switch",
        "this",
        "throw",
        "true",
        "try",
        "typeof",
        "var",
        "void",
        "while",
        "with",
        "implements",
        "interface",
        "let",
        "package",
        "private",
        "protected",
        "public",
        "static",
        "yield",
        "any",
        "boolean",
        "number",
        "string",
        "symbol",
        "abstract",
        "as",
        "async",
        "await",
        "constructor",
        "declare",
        "from",
        "get",
        "is",
        "module",
        "namespace",
        "of",
        "require",
        "set",
        "type"
)

fun toVariableName(vararg parts: String) =
        toLowerCamelCase(*escapeReservedWordsAndChars(replacementChars, reservedWords, *parts).toTypedArray())
