package io.github.fomin.oasgen

enum class OutputFileType {
    DTO, ROUTE
}

data class OutputFile(
        val path: String,
        val content: String,
        val type: OutputFileType
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

        return true
    }

    override fun hashCode(): Int {
        return fragment.hashCode()
    }

}

interface Writer<T> {
    fun write(
            items: Iterable<T>
    ): List<OutputFile>
}
