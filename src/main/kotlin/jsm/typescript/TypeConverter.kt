package jsm.typescript

import jsm.JsonSchema
import jsm.JsonType
import jsm.indentWithMargin
import jsm.java.jackson.jointProperties
import jsm.java.toLowerCamelCase
import jsm.java.toTypeName
import jsm.java.toUpperCamelCase

interface JsonConverter {
    fun toJson(valueExpression: String): String
    fun fromJson(valueExpression: String): String
    fun content(): String?
}

interface TypeConverter {
    fun type(): String
    fun content(): String?
    fun importDeclarations(): List<ImportDeclaration>
    fun innerSchemas(): List<JsonSchema>
    val jsonConverter: JsonConverter?
}

interface TypeConverterMatcher {
    fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter?
}

class TypeConverterRegistry(private val converterMatchers: List<TypeConverterMatcher>) {
    operator fun get(jsonSchema: JsonSchema): TypeConverter {
        converterMatchers.forEach { typeConverterMatcher ->
            val typeConverter = typeConverterMatcher.match(this, jsonSchema)
            if (typeConverter != null) return typeConverter
        }
        error("Can't find converter for schema $jsonSchema")
    }
}

class StringConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.STRING -> object : TypeConverter {
            override fun type() = "string"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}

class IntegerConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.INTEGER -> object : TypeConverter {
            override fun type() = "integer"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}

class NumberConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.NUMBER -> object : TypeConverter {
            override fun type() = "number"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}

class BooleanConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when (jsonSchema.type) {
        JsonType.Scalar.BOOLEAN -> object : TypeConverter {
            override fun type() = "boolean"
            override fun content(): String? = null
            override fun importDeclarations() = emptyList<ImportDeclaration>()
            override fun innerSchemas() = emptyList<JsonSchema>()
            override val jsonConverter: JsonConverter? = null
        }
        else -> null
    }
}

class LocalDateTimeConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema) = when {
        jsonSchema.type == JsonType.Scalar.STRING && jsonSchema.format == "local-date-time" -> object : TypeConverter {
            override fun type() = "LocalDateTime"
            override fun content(): String? = null
            override fun importDeclarations() = listOf(ImportDeclaration("LocalDateTime", "@js-joda/core"))
            override fun innerSchemas() = listOf<JsonSchema>()
            override val jsonConverter = object : JsonConverter {
                override fun toJson(valueExpression: String) = "$valueExpression.toString()"
                override fun fromJson(valueExpression: String) = "LocalDateTime.parse($valueExpression)"
                override fun content(): String? = null
            }

        }
        else -> null
    }
}

class ArrayConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        return if (jsonSchema.type == JsonType.ARRAY) {
            object : TypeConverter {
                val itemsSchema = jsonSchema.items() ?: error("items is required in $jsonSchema")
                val itemsTypeConverter = typeConverterRegistry[itemsSchema]
                val itemsJsonConverter = itemsTypeConverter.jsonConverter

                override fun type() = "readonly ${itemsTypeConverter.type()}[]"

                override fun content(): String? = null

                override fun importDeclarations() = listOf<ImportDeclaration>()

                override fun innerSchemas() = listOf(itemsSchema)

                override val jsonConverter = if (itemsJsonConverter != null) {
                    object : JsonConverter {
                        override fun toJson(valueExpression: String) = "TODO"

                        override fun fromJson(valueExpression: String) = "TODO"

                        override fun content() = "TODO"
                    }
                } else {
                    null
                }

            }
        } else {
            null
        }
    }
}

class MapConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        val valueSchema = jsonSchema.additionalProperties()
        return if (jsonSchema.type == JsonType.OBJECT && valueSchema != null) {
            object : TypeConverter {
                val valueTypeConverter = typeConverterRegistry[valueSchema]
                val valueJsonConverter = valueTypeConverter.jsonConverter

                override fun type() = "Map<string, ${toLowerCamelCase(valueTypeConverter.type())}>"

                override fun content(): String? = null

                override fun importDeclarations() = emptyList<ImportDeclaration>()

                override fun innerSchemas() = listOf(valueSchema)

                override val jsonConverter = if (valueJsonConverter != null) {
                    object : JsonConverter {
                        override fun toJson(valueExpression: String): String {
                            TODO("Not yet implemented")
                        }

                        override fun fromJson(valueExpression: String): String {
                            TODO("Not yet implemented")
                        }

                        override fun content(): String = ""

                    }
                } else {
                    null
                }

            }
        } else {
            null
        }
    }
}

class StringEnumConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        val enum = jsonSchema.enum()
        return when {
            jsonSchema.type == JsonType.Scalar.STRING && enum != null -> object : TypeConverter {
                val typeName = toTypeName(jsonSchema)

                override fun type() = toUpperCamelCase(typeName.name)

                override fun content(): String? {
                    val valueExpressions = enum.map { value -> "${toUpperCamelCase(value)} = \"$value\"" }
                    return """ |/**
                               | * ${jsonSchema.title}
                               | */
                               |export const enum ${type()} {
                               |    ${valueExpressions.joinToString(",\n").indentWithMargin(1)}
                               |}""".trimMargin()
                }
                override fun importDeclarations() = emptyList<ImportDeclaration>()

                override fun innerSchemas() = emptyList<JsonSchema>()

                override val jsonConverter: JsonConverter? = null

            }
            else -> null
        }
    }

}

class ObjectConverterMatcher : TypeConverterMatcher {
    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        return if (jsonSchema.type == JsonType.OBJECT) {
            object : TypeConverter {
                val typeName = toTypeName(jsonSchema)
                val hasMappedToJsonProperties = jsonSchema.jointProperties().any {
                    typeConverterRegistry[it.value].jsonConverter != null
                }

                override fun type() = toUpperCamelCase(typeName.name)

                override fun content(): String? {
                    val fieldDeclarations = jsonSchema.jointProperties().map { (propertyName, propertySchema) ->
                        """|/**
                           | * ${propertySchema.title}
                           | */
                           |readonly ${toLowerCamelCase(propertyName)}: ${typeConverterRegistry[propertySchema].type()};
                           |""".trimMargin()
                    }

                    return """ |/**
                               | * ${jsonSchema.title}
                               | */
                               |export interface ${toUpperCamelCase(typeName.name)} {
                               |    ${fieldDeclarations.indentWithMargin(1)}
                               |}""".trimMargin()
                }

                override fun importDeclarations() =
                        if (hasMappedToJsonProperties)
                            listOf(ImportDeclaration("mapObjectProperties", "jsm-support"))
                        else emptyList()

                override fun innerSchemas() = jsonSchema.jointProperties().values.toList()

                override val jsonConverter = when {
                    hasMappedToJsonProperties -> object : JsonConverter {
                        override fun toJson(valueExpression: String) =
                                "${toLowerCamelCase(typeName.name, "to", "json")}($valueExpression)"

                        override fun fromJson(valueExpression: String) =
                                "${toLowerCamelCase(typeName.name, "from", "json")}($valueExpression)"

                        private fun switchCases(converterExpression: (JsonConverter) -> String) = jsonSchema.jointProperties().mapNotNull { (propertyName, propertySchema) ->
                            val propertyJsonConverter = typeConverterRegistry[propertySchema].jsonConverter
                            if (propertyJsonConverter != null) {
                                """|case "$propertyName":
                                   |    return ${converterExpression(propertyJsonConverter)};
                                   |""".trimMargin()
                            } else {
                                null
                            }
                        }

                        override fun content() =
                                """|function ${toLowerCamelCase(typeName.name, "from", "json")}(json: any): ${type()} {
                                   |    return mapObjectProperties(json, (key, value) => {
                                   |        switch (key) {
                                   |            ${switchCases { it.fromJson("value") }.indentWithMargin(3)}
                                   |            default:
                                   |                return value;
                                   |        }
                                   |    });
                                   |}
                                   |
                                   |function ${toLowerCamelCase(typeName.name, "to", "json")}(obj: ${type()}): any {
                                   |    return mapObjectProperties(obj, (key, value) => {
                                   |        switch (key) {
                                   |            ${switchCases { it.toJson("value") }.indentWithMargin(3)}
                                   |            default:
                                   |                return value;
                                   |        }
                                   |    });
                                   |}""".trimMargin()

                    }
                    else -> null
                }
            }
        } else null
    }
}
