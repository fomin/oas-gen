package io.github.fomin.oasgen

data class OutputFile(
        val path: String,
        val content: String
)

abstract class TypedFragment {
    abstract val fragment: Fragment
    abstract val parent: TypedFragment?
    override fun toString() = "${javaClass.simpleName}(${fragment.reference})"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypedFragment

        if (fragment != other.fragment) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fragment.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }

}

interface Writer<T> {
    fun write(
            items: Iterable<T>
    ): List<OutputFile>
}
