package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

class EnumConverterMatcher(val basePackage: String) : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        val enumValues = jsonSchema.enum()
        return if (jsonSchema.type is JsonType.Scalar.STRING && enumValues != null) object : Converter {
            override val jsonSchema = jsonSchema
            override fun valueType() = toJavaClassName(basePackage, jsonSchema)
            override fun extraAnnotations(): String? = null
            override fun stringParseExpression(valueExpression: String) = "${valueType()}.parseString($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) = "${valueType()}.writeString($valueExpression)"
            override fun output(): ConverterOutput {
                val simpleName = getSimpleName(valueType())

                val valueDeclarations = enumValues.joinToString(",\n") { enumValue ->
                    """${toUpperSnakeCase(enumValue)}("$enumValue")"""
                }

                val parserCases = enumValues.map { enumValue ->
                    """|case "$enumValue":
                       |    return ${toUpperSnakeCase(enumValue)};""".trimMargin()
                }

                val content =
                        """|package ${getPackage(valueType())};
                           |
                           |import com.fasterxml.jackson.annotation.JsonValue;
                           |import javax.annotation.Nonnull;
                           |
                           |/**
                           | * ${jsonSchema.title}
                           | */
                           |public enum $simpleName {
                           |
                           |    ${valueDeclarations.indentWithMargin(1)};
                           |
                           |    @Nonnull
                           |    @JsonValue
                           |    public final String strValue;
                           |
                           |    $simpleName(@Nonnull String strValue) {
                           |        this.strValue = strValue;
                           |    }
                           |
                           |    public static $simpleName parseString(String value) {
                           |        switch (value) {
                           |            ${parserCases.indentWithMargin(3)}
                           |            default:
                           |                throw new UnsupportedOperationException("Unsupported value " + value);
                           |        }
                           |    }
                           |
                           |    public static String writeString($simpleName value) {
                           |        return value.strValue;
                           |    }
                           |
                           |}
                           |
                        """.trimMargin()
                val filePath = getFilePath(valueType())
                return ConverterOutput(OutputFile(filePath, content), emptyList())
            }
        }
        else null
    }
}
