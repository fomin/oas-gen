package io.github.fomin.oasgen

import io.github.fomin.oasgen.java.toUpperCamelCase
import java.net.URLDecoder

data class TypeName(
        val namespace: List<String>,
        val name: String
) {
    companion object {
        fun toTypeName(typedFragment: TypedFragment, prefix: String? = null, suffix: String? = null): TypeName {
            val nameParts = mutableListOf<String?>()

            var currentFragment = typedFragment

            do {
                val fragmentReference = currentFragment.fragment.reference
                if (fragmentReference.fragmentPath.isEmpty()) {
                    val lastName = URLDecoder.decode(fragmentReference.filePath, "UTF-8")
                        .split("/").last()
                    val dotIndex = lastName.indexOf('.')
                    val namePart = if (dotIndex == -1) lastName else lastName.substring(0, dotIndex)
                    nameParts.add(namePart)
                    if (!namePart[0].isLetter()) {
                        nameParts.add("$")
                    }
                    break
                }

                if ((fragmentReference.fragmentPath.size == 3
                                && fragmentReference.fragmentPath[0] == "components"
                                && fragmentReference.fragmentPath[1] == "schemas")
                        || (fragmentReference.fragmentPath.size == 6
                                && fragmentReference.fragmentPath[0] == "components"
                                && fragmentReference.fragmentPath[1] == "responses")
                ) {

                    if (fragmentReference.fragmentPath[1] == "responses") {
                        nameParts.add("response")
                    }

                    val componentName = fragmentReference.fragmentPath[2]
                    nameParts.add(componentName)
                    if (!componentName[0].isLetter()) {
                        nameParts.add("$")
                    }
                    break
                } else if (fragmentReference.fragmentPath.size == 2
                    && fragmentReference.fragmentPath[0] == "definitions") {

                    val componentName = fragmentReference.fragmentPath[1]
                    nameParts.add(componentName)
                    if (!componentName[0].isLetter()) {
                        nameParts.add("$")
                    }
                    break
                }

                val parent = currentFragment.parent ?: break
                val grandParent = parent.parent
                if (parent is Parameter) {
                    if (grandParent is Operation) {
                        nameParts.add(grandParent.operationId)
                        nameParts.add("of")
                    }
                    nameParts.add(parent.name)
                    break
                } else if (parent is MediaTypeObject) {
                    if (grandParent is RequestBody) {
                        nameParts.add("request")
                        val requestBodyParent = grandParent.parent
                        if (requestBodyParent is Operation) {
                            nameParts.add(requestBodyParent.operationId)
                        }
                        break
                    } else if (grandParent is Response) {
                        nameParts.add("response")
                        val responseParent = grandParent.parent?.parent
                        if (responseParent is Operation) {
                            nameParts.add(responseParent.operationId)
                        }
                        break
                    }
                }

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

            nameParts.add(prefix)
            nameParts.reverse()
            nameParts.add(suffix)
            val simpleName = toUpperCamelCase(*nameParts.filterNotNull().toTypedArray())
            val pathComponents = typedFragment.fragment.reference.filePath.split('/')
            val namespace = toNamespace(pathComponents)
            return TypeName(namespace, simpleName)
        }
    }
}

private fun toNamespace(pathComponents: List<String>): List<String> {
    val namespace = mutableListOf<String>()
    for (i in 0..pathComponents.size - 2) {
        val pathComponent = pathComponents[i]
        namespace.add(pathComponent.toLowerCase().replace("-", ""))
    }
    return namespace
}