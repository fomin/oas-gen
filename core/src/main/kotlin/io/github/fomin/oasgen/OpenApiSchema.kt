package io.github.fomin.oasgen

enum class OperationType {
    GET, DELETE, POST, PUT;

    fun key() = name.toLowerCase()
}

class OpenApiSchema(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun paths() = Paths(fragment["paths"], this)
}

class Paths(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun pathItems() = fragment.map { key, _ ->
        Pair(key, PathItem(fragment[key], this))
    }.toMap()
}

class PathItem(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun operations() = OperationType.values().mapNotNull { operationType ->
        fragment.getOptional(operationType.key())?.let { Operation(it, this, operationType) }
    }
}

class Operation(
        override val fragment: Fragment,
        override val parent: TypedFragment?,
        val operationType: OperationType
) : TypedFragment() {
    val operationId = fragment["operationId"].asString()

    fun parameters() = when (val parametersFragment = fragment.getOptional("parameters")) {
        null -> emptyList()
        else -> parametersFragment.map { fragment ->
            Parameter(fragment, this)
        }
    }

    fun requestBody() = fragment.getOptional("requestBody")?.let { RequestBody(it, this) }

    fun responses() = Responses(fragment["responses"], this)
}

class RequestBody(override val fragment: Fragment, override val parent: TypedFragment?): TypedFragment() {
    val description = fragment.getOptional("description")?.asString()
    val required = fragment.getOptional("required")?.asBoolean() ?: false

    fun content() = fragment["content"].map { mediaType, mediaTypeFragment ->
        Pair(mediaType, MediaTypeObject(mediaTypeFragment, this))
    }.toMap()

}

enum class ParameterIn {
    PATH, QUERY
}

class Parameter(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    val name = fragment["name"].asString()
    val parameterIn = ParameterIn.valueOf(fragment["in"].asString().toUpperCase())
    val required = fragment.getOptional("required")?.asBoolean() ?: false

    fun schema() = JsonSchema(fragment["schema"], this)
}

class Responses(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun byCode() =
        fragment.map { key, responseFragment ->
            Pair(key, Response(responseFragment, this))
        }.toMap()

    fun default() = byCode()["default"]

    fun singleOrNull2xx() = byCode().entries.singleOrNull { it.key.startsWith("2") }
}

class Response(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun content() = fragment.getOptional("content")?.map { mediaType, mediaTypeFragment ->
        Pair(mediaType, MediaTypeObject(mediaTypeFragment, this))
    }?.toMap() ?: emptyMap()
}

class MediaTypeObject(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun schema() = JsonSchema(fragment["schema"], this)
}
