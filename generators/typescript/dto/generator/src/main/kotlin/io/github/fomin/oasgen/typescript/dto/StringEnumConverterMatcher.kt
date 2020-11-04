package io.github.fomin.oasgen.typescript.dto

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType
import io.github.fomin.oasgen.TypeName
import io.github.fomin.oasgen.indentWithMargin
import io.github.fomin.oasgen.java.toUpperCamelCase
import io.github.fomin.oasgen.typescript.jsDoc

class StringEnumConverterMatcher(
    private val namingStrategy: NamingStrategy
) : TypeConverterMatcher {
    class Provider : TypeConverterMatcherProvider {
        override val id = "enum"
        override fun provide() = StringEnumConverterMatcher(DefaultNamingStrategy())
    }

    override fun match(typeConverterRegistry: TypeConverterRegistry, jsonSchema: JsonSchema): TypeConverter? {
        val enum = jsonSchema.enum()
        return when {
            jsonSchema.type == JsonType.Scalar.STRING && enum != null -> object : TypeConverter {
                val typeName = namingStrategy.typeName(jsonSchema)

                override fun type() = toUpperCamelCase(typeName.name)

                override fun content(): String? {
                    val valueExpressions = enum.map { value -> "${toUpperCamelCase(value)} = \"$value\"" }
                    return """ |${jsDoc(jsonSchema)}
                               |export enum ${type()} {
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