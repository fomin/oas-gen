package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType
import io.github.fomin.oasgen.indentWithMargin
import io.github.fomin.oasgen.java.toTypeName
import io.github.fomin.oasgen.java.toUpperCamelCase
import io.github.fomin.oasgen.typescript.toVariableName
import io.github.fomin.oasgen.jointProperties

class ObjectConverterMatcher : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "object"
        override fun provide() = ObjectConverterMatcher()
    }

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
                           |readonly ${toVariableName(propertyName)}: ${typeConverterRegistry[propertySchema].type()};
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
                            listOf(ImportDeclaration("mapObjectProperties", "@andrey.n.fomin/oas-gen-typescript-dto-runtime"))
                        else emptyList()

                override fun innerSchemas() = jsonSchema.jointProperties().values.toList()

                override val jsonConverter = when {
                    hasMappedToJsonProperties -> object : JsonConverter {
                        override fun toJson(valueExpression: String) =
                                "${toVariableName(typeName.name, "to", "json")}($valueExpression)"

                        override fun fromJson() =
                                toVariableName(typeName.name, "from", "json")

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
                                """|// @ts-ignore
                                   |function ${toVariableName(typeName.name, "from", "json")}(json: any): ${type()} {
                                   |    return mapObjectProperties(json, (key, value) => {
                                   |        switch (key) {
                                   |            ${switchCases { "${it.fromJson()}(value)" }.indentWithMargin(3)}
                                   |            default:
                                   |                return value;
                                   |        }
                                   |    });
                                   |}
                                   |
                                   |// @ts-ignore
                                   |function ${toVariableName(typeName.name, "to", "json")}(obj: ${type()}): any {
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