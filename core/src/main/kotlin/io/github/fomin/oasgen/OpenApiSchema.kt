package io.github.fomin.oasgen

enum class OperationType {
    GET, POST, DELETE;

    fun key() = name.toLowerCase()
}

enum class HttpResponseCode {
    CODE_200, CODE_500;

    fun key() = name.substring(5)
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

    fun schema() = JsonSchema(fragment["schema"], this)
}

class Responses(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun default() = fragment.getOptional("default")?.let { Response(it, this) }

    fun byCode() = HttpResponseCode.values().mapNotNull { httpResponseCode ->
        fragment.getOptional(httpResponseCode.key())?.let { Pair(httpResponseCode, Response(it, this)) }
    }.toMap()
}

class Response(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun content() = fragment.getOptional("content")?.map { mediaType, mediaTypeFragment ->
        Pair(mediaType, MediaTypeObject(mediaTypeFragment, this))
    }?.toMap() ?: emptyMap()
}

class MediaTypeObject(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    fun schema() = JsonSchema(fragment["schema"], this)
}
