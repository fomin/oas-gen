// TODO move out of java package
package io.github.fomin.oasgen.java

import io.github.fomin.oasgen.*

fun toJavaClassName(basePackage: String, typedFragment: TypedFragment, suffix: String? = null): String {
    val typeName = toTypeName(typedFragment, suffix)
    val package_ = typeName.namespace.joinToString("") {
        when {
            reservedWords.contains(it) -> "${it.toLowerCase()}$."
            else -> "${it.toLowerCase()}."
        }
    }
    val escapedName = when (val name = typeName.name) {
        in reservedWords -> "$name$"
        else -> name
    }
    return "$basePackage.$package_$escapedName"
}

data class TypeName(
        val namespace: List<String>,
        val name: String
)

fun toTypeName(typedFragment: TypedFragment, suffix: String? = null): TypeName {
    val nameParts = mutableListOf<String?>()

    var currentFragment = typedFragment

    do {
        val fragmentReference = currentFragment.fragment.reference
        if (fragmentReference.fragmentPath.isEmpty()) {
            val lastName = fragmentReference.filePath.split("/").last()
            val dotIndex = lastName.indexOf('.')
            val namePart = if (dotIndex == -1) lastName else lastName.substring(0, dotIndex)
            nameParts.add(namePart)
            break
        }

        val parent = currentFragment.parent ?: break

        if (parent is JsonSchema && parent.type == JsonType.ARRAY) {
            currentFragment = parent
            continue
        }
        nameParts.add(fragmentReference.fragmentPath.last())

        if (!parent.fragment.reference.isAncestorOf(fragmentReference)) {
            break
        }
        currentFragment = parent
    } while (true)

    nameParts.reverse()
    val simpleName = toUpperCamelCase(*(nameParts + suffix).filterNotNull().toTypedArray())
    val pathComponents = typedFragment.fragment.reference.filePath.split('/')
    val namespace = toNamespace(pathComponents)
    return TypeName(namespace, simpleName)
}

fun toMethodName(vararg parts: String) = toLowerCamelCase(*parts)

val replacementChars = listOf(
        "@" to "At-",
        "/" to "Slash-"
)

val reservedWords = setOf(
        "_",
        "abstract",
        "assert",
        "boolean",
        "break",
        "byte",
        "case",
        "catch",
        "char",
        "class",
        "const",
        "continue",
        "default",
        "do",
        "double",
        "else",
        "enum",
        "extends",
        "false",
        "final",
        "finally",
        "float",
        "for",
        "goto",
        "if",
        "implements",
        "import",
        "instanceof",
        "int",
        "interface",
        "long",
        "native",
        "new",
        "null",
        "package",
        "private",
        "protected",
        "public",
        "return",
        "short",
        "static",
        "strictfp",
        "super",
        "switch",
        "synchronized",
        "this",
        "throw",
        "throws",
        "transient",
        "true",
        "try",
        "var",
        "void",
        "volatile",
        "while"
)

fun toVariableName(vararg parts: String): String {
    val filteredParts = parts.map { part ->
        replacementChars.fold(part) {acc, replacementPair ->
            acc.replace(replacementPair.first, replacementPair.second)
        }
    }

    return when (filteredParts.size) {
        1 -> {
            val singlePart = filteredParts[0]
            when {
                reservedWords.contains(singlePart) -> toLowerCamelCase(singlePart, "$")
                else -> toLowerCamelCase(singlePart)
            }
        }
        else -> toLowerCamelCase(*filteredParts.toTypedArray())
    }
}

//private fun toPackagePart(pathComponents: List<String>): String {
//    val sb = StringBuilder()
//    for (i in 0..pathComponents.size - 2) {
//        val pathComponent = pathComponents[i]
//        sb.append(pathComponent.toLowerCase().replace("-", ""))
//        sb.append(".")
//    }
//    return sb.toString()
//}

private fun toNamespace(pathComponents: List<String>): List<String> {
    val namespace = mutableListOf<String>()
    for (i in 0..pathComponents.size - 2) {
        val pathComponent = pathComponents[i]
        namespace.add(pathComponent.toLowerCase().replace("-", ""))
    }
    return namespace
}

fun toUpperSnakeCase(vararg parts: String): String {
    val sb = StringBuilder()
    parts.forEachIndexed { index, part ->
        if (index > 0) sb.append("_")
        part.forEach { char ->
            when {
                char == '-' -> sb.append('_')
                char.isUpperCase() -> sb.append("_").append(char)
                else -> sb.append(char.toUpperCase())
            }
        }
    }
    return sb.toString()
}

fun toUpperCamelCase(vararg parts: String) = toCamelCase(true, *parts)

fun toLowerCamelCase(vararg parts: String) = toCamelCase(false, *parts)

fun toCamelCase(firstUpper: Boolean, vararg parts: String): String {
    val sb = StringBuilder()
    var nextInUpper = firstUpper
    parts.forEachIndexed { partIndex, part ->
        if (partIndex > 0) nextInUpper = true
        part.forEachIndexed { charIndex, char ->
            nextInUpper = if (char in listOf('-', '.')) {
                true
            } else {
                if (nextInUpper) {
                    sb.append(char.toUpperCase())
                } else {
                    if (!firstUpper && partIndex == 0 && charIndex == 0)
                        sb.append(char.toLowerCase())
                    else
                        sb.append(char)
                }
                false
            }
        }
    }
    return sb.toString()
}

fun getFilePath(className: String): String {
    return "${className.replace('.', '/')}.java"
}

fun getPackage(className: String): String {
    return className.substring(0, className.lastIndexOf('.'))
}

fun getSimpleName(className: String): String {
    return className.substring(className.lastIndexOf('.') + 1)
}
