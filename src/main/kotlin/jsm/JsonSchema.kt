package jsm

sealed class JsonType(val name: String) {
    sealed class Scalar(name: String) : JsonType(name) {
        object STRING : Scalar("string")
        object NUMBER : Scalar("number")
        object INTEGER : Scalar("integer")
        object BOOLEAN : Scalar("boolean")
    }
    object ARRAY : JsonType("array")
    object OBJECT : JsonType("object")
}

fun getJsonType(type: String): JsonType {
    return when (type) {
        "string" -> JsonType.Scalar.STRING
        "number" -> JsonType.Scalar.NUMBER
        "integer" -> JsonType.Scalar.INTEGER
        "boolean" -> JsonType.Scalar.BOOLEAN
        "array" -> JsonType.ARRAY
        "object" -> JsonType.OBJECT
        else -> error("unknown type $type")
    }
}

class JsonSchema(override val fragment: Fragment, override val parent: TypedFragment?) : TypedFragment() {
    val title = fragment.getOptional("title")?.asString()
    val type = getJsonType(fragment["type"].asString())
    val format = fragment.getOptional("format")?.asString()

    fun properties() = fragment.getOptional("properties")?.map { propertyName, propertyFragment ->
        propertyName to JsonSchema(propertyFragment, this)
    }?.toMap() ?: emptyMap()

    fun items() = fragment.getOptional("items")?.let { JsonSchema(it, this) }

    fun allOf() = fragment.getOptional("allOf")?.map { itemFragment ->
        JsonSchema(itemFragment, this)
    }

    fun enum() = fragment.getOptional("enum")?.map { enumValue -> enumValue.asString() }

    fun additionalProperties(): JsonSchema? {
        val additionalPropertiesFragment = fragment.getOptional("additionalProperties")
        if (additionalPropertiesFragment == null) {
            return null
        } else if (additionalPropertiesFragment.value is Boolean) {
            return null
        } else {
            return JsonSchema(additionalPropertiesFragment, this)
        }
    }
}
