package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.toUpperCamelCase
import io.github.fomin.oasgen.typescript.jsDoc
import io.github.fomin.oasgen.typescript.toVariableName

class ObjectConverterMatcher(
    private val namingStrategy: NamingStrategy
) : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "object"
        override fun provide() = ObjectConverterMatcher(DefaultNamingStrategy())
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        return if (jsonSchema.type == JsonType.OBJECT) {
            object : TypeConverter {
                val typeName = namingStrategy.typeName(jsonSchema)
                val hasMappedToJsonProperties = jsonSchema.jointProperties().any {
                    typeConverterRegistry[it.value].jsonConverter != null
                }

                override fun type() = toUpperCamelCase(typeName.name)

                override fun content(): String? {
                    val fieldDeclarations = jsonSchema.jointProperties().map { (propertyName, propertySchema) ->
                        val optionalFlag = if (!jsonSchema.required().contains(propertyName)) "?" else ""

                        """|${jsDoc(propertySchema)}
                           |"$propertyName"$optionalFlag: ${typeConverterRegistry[propertySchema].type()};
                           |""".trimMargin()
                    }

                    return """ |${jsDoc(jsonSchema)}
                               |export interface ${toUpperCamelCase(typeName.name)} {
                               |    ${fieldDeclarations.indentWithMargin(1)}
                               |}""".trimMargin()
                }

                override fun importDeclarations() = emptyList<ImportDeclaration>()

                override fun innerSchemas() = jsonSchema.jointProperties().values.toList()

                override val jsonConverter = when {
                    hasMappedToJsonProperties -> object : JsonConverter {
                        override fun toJson(valueExpression: String) =
                                "${toVariableName(typeName.name, "to", "json")}($valueExpression)"

                        override fun fromJson(valueExpression: String) =
                                "${toVariableName(typeName.name, "from", "json")}($valueExpression)"

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
                                   |            ${switchCases { it.fromJson("value") }.indentWithMargin(3)}
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