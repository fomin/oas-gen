package io.github.fomin.oasgen

import java.io.File
import java.net.URLDecoder

class Fragment(private val fragmentRegistry: FragmentRegistry, val reference: Reference, val value: Any) {

    fun asMap() = when (value) {
        is Map<*, *> -> value
        else -> error("reference $reference doesn't point to map")
    }

    fun asList() = when (value) {
        is List<*> -> value
        else -> error("reference $reference doesn't point to list")
    }

    fun asString() = when (value) {
        is String -> value
        else -> error("reference $reference doesn't point to string")
    }

    fun asBoolean() = when (value) {
        is Boolean -> value
        is String -> value.toBoolean()
        else -> error("reference $reference doesn't point to boolean")
    }

    fun getOptional(vararg elements: String) = fragmentRegistry.getOptional(reference.get(*elements))

    operator fun get(vararg elements: String) = when (val fragment = this.getOptional(*elements)) {
        null -> error("can't find element by reference ${reference.get(*elements)}")
        else -> fragment
    }

    operator fun get(i: Int) = get(i.toString())

    fun parent() = reference.parent()?.let { fragmentRegistry.getOptional(it) }

    inline fun forEachIndexed(action: (index: Int, fragment: Fragment) -> Unit) = when (value) {
        is List<*> -> value.forEachIndexed { index, _ -> action(index, get(index)) }
        else -> error("method can be called only for list or empty references $reference")
    }

    inline fun forEach(action: (key: String, fragment: Fragment) -> Unit) = when (value) {
        is Map<*, *> -> value.forEach { (k, _) -> action(k as String, get(k)) }
        else -> error("method can be called only for map or empty references $reference")
    }

    inline fun <R> map(transform: (key: String, fragment: Fragment) -> R) = when (value) {
        is Map<*, *> -> value.map { (k, _) -> transform(k as String, get(k)) }
        else -> error("method can be called only for map or empty references $reference")
    }

    inline fun <R> map(transform: (fragment: Fragment) -> R) = when (value) {
        is List<*> -> value.mapIndexed { index, _ -> transform(this[index]) }
        else -> error("method can be called only for list or empty references $reference")
    }

    inline fun <R> mapIndexed(transform: (index: Int, fragment: Fragment) -> R) = when (value) {
        is List<*> -> value.mapIndexed { index, _ -> transform(index, this[index]) }
        else -> error("method can be called only for list or empty references $reference")
    }

    override fun toString(): String {
        return "Fragment(reference=$reference, valueType=${value.javaClass})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fragment

        if (reference != other.reference) return false

        return true
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }

}

data class Reference(
        val filePath: String,
        val fragmentPath: List<String>
) {
    fun resolve(other: String): Reference {
        val parts = other.split("#")
        val (pathStr, fragmentStr) = when (parts.size) {
            2 -> Pair(parts[0], parts[1])
            else -> Pair(parts[0], "/")
        }
        val newPath = when (pathStr) {
            "" -> filePath
            else -> File(filePath).parentFile.toURI().resolve(pathStr).normalize().toString()
        }
        val fragments = fragmentStr
                .split("/")
                .filter { it.isNotEmpty() }
                .map { URLDecoder.decode(it, "UTF-8") }
        return Reference(newPath, fragments)
    }

    fun root() = Reference(filePath, emptyList())

    operator fun get(vararg elements: String) = Reference(filePath, fragmentPath + elements)

    fun parent(): Reference? {
        val size = fragmentPath.size
        return when {
            size > 0 -> Reference(filePath, fragmentPath.take(size - 1))
            else -> null
        }
    }

    override fun toString() = "Reference($filePath#${fragmentPath.joinToString("") { "/$it" }})"

    companion object {
        fun root(filePath: String) = Reference(filePath, emptyList())
    }

    fun isAncestorOf(other: Reference): Boolean {
        return this.filePath == other.filePath
                && this.fragmentPath.size < other.fragmentPath.size
                && this.fragmentPath == other.fragmentPath.subList(0, this.fragmentPath.size)
    }
}

data class RootFragment(
        val path: String,
        val fragment: Map<*, *>
)

class FragmentRegistry(rootFragments: Iterable<RootFragment>) {
    private val index = mutableMapOf<Reference, Any?>()

    init {
        rootFragments.forEach { rootFragment ->
            index[Reference.root(rootFragment.path)] = rootFragment.fragment
        }
    }

    fun getOptional(reference: Reference): Fragment? {
        val resolvedReference = resolveRecursively(reference)
        return when (val value = this.getValue(resolvedReference)) {
            null -> null
            else -> Fragment(this, resolvedReference, value)
        }
    }

    fun get(reference: Reference) = getOptional(reference) ?: error("reference not found $reference")

    fun getValue(reference: Reference): Any? {
        val existingValue = index[reference]
        if (existingValue != null) {
            return existingValue
        } else {
            val rootFragment = index[reference.root()]
            if (rootFragment != null) {
                var currentValue: Any? = rootFragment
                reference.fragmentPath.forEach { key ->
                    val currentValueLocal = currentValue ?: return null
                    if (key != "") currentValue = when (currentValueLocal) {
                        is Map<*, *> -> currentValueLocal[key]
                        is List<*> -> currentValueLocal[key.toInt()]
                        else -> error("Can't get inner elements of ${currentValueLocal.javaClass} " +
                                "for reference $reference")
                    }
                }
                index[reference] = currentValue
                return currentValue
            } else {
                error("Can't find root fragment for reference $reference")
            }
        }
    }

    private tailrec fun resolveRecursively(reference: Reference): Reference {
        val fragment = getValue(reference)

        return if (fragment is Map<*, *>) {
            val ref = fragment["${"$"}ref"]
            if (ref is String) {
                resolveRecursively(reference.resolve(ref))
            } else {
                reference
            }
        } else {
            reference
        }
    }

}
