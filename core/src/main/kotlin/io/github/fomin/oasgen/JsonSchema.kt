package io.github.fomin.oasgen

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
    val description = fragment.getOptional("description")?.asString()
    val type = getJsonType(fragment["type"].asString())
    val format = fragment.getOptional("format")?.asString()

    fun properties() = fragment.getOptional("properties")?.map { propertyName, propertyFragment ->
        propertyName to JsonSchema(propertyFragment, this)
    }?.toMap() ?: emptyMap()

    fun items() = fragment.getOptional("items")?.let { JsonSchema(it, this) }

    fun allOf() = when (val allOfFragment = fragment.getOptional("allOf")) {
        null -> emptyList()
        else -> allOfFragment.map { itemFragment ->
            JsonSchema(itemFragment, this)
        }
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

    fun required() = when (val requiredFragment = fragment.getOptional("required")) {
        null -> emptySet()
        else -> requiredFragment.map { propertyNameFragment -> propertyNameFragment.asString() }.toSet()
    }
}

fun JsonSchema.jointRequired() = when (this.type) {
    JsonType.OBJECT -> (required() + allOf().flatMap { it.required() }).toSet()
    else -> error("This method can be called only for objects")
}

private fun JsonSchema.jointPropertiesList(): List<Pair<String, JsonSchema>> =
        if (this.type == JsonType.OBJECT) {
            val innerPropertiesList = allOf().flatMap { innerSchema ->
                innerSchema.jointPropertiesList()
            }
            val propertiesList = properties().toList()
            innerPropertiesList + propertiesList
        } else error("This method can be called only for objects")


fun JsonSchema.jointProperties(): Map<String, JsonSchema> = jointPropertiesList()
        .fold(mutableMapOf()) { acc, propertyPair ->
            val propertyName = propertyPair.first
            val propertySchema = propertyPair.second
            val previousValue = acc.put(propertyName, propertySchema)
            if (previousValue != null) {
                error("Found duplicated property $propertyName in schema $this")
            }
            acc
        }

fun JsonSchema.obfuscatedProperties(): List<String> {
    val properties: Map<String, JsonSchema> = properties()
    var obfuscatedProperties = mutableListOf<String>()
    for (p in properties.keys) {
        val props = properties?.get(p)?.fragment?.value as LinkedHashMap<String, *>
        if (props?.get("x-obfuscated") != null && props?.get("x-obfuscated") as Boolean) {
            obfuscatedProperties.add(p)
        }
    }
    return obfuscatedProperties
}
