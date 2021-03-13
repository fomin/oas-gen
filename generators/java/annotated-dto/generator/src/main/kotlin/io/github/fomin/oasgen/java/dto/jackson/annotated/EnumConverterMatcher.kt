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
                val enumLabels = jsonSchema.enumLabel() ?: enumValues
                val enumValueType = getJavaTypeName(jsonSchema.format)
                val valueDeclarations = enumLabels.withIndex().joinToString(",\n") { (index, enumLabel) ->
                    """${toUpperSnakeCase(enumLabel.toString())}(${toVariableType(jsonSchema.format, enumValues[index])})"""
                }

                val parserCases = enumLabels.withIndex().map { (index, enumLabel) ->
                    """|case ${toVariableType(jsonSchema.format, enumValues[index])}:
                       |    return ${toUpperSnakeCase(enumLabel.toString())};""".trimMargin()
                }

                val content =
                        """|package ${getPackage(valueType())};
                           |
                           |import com.fasterxml.jackson.annotation.JsonValue;
                           |import javax.annotation.Nonnull;
                           |
                           |${javaDoc(jsonSchema)}
                           |public enum $simpleName {
                           |
                           |    ${valueDeclarations.indentWithMargin(1)};
                           |
                           |    @Nonnull
                           |    @JsonValue
                           |    public final ${enumValueType.first} ${enumValueType.second}Value; 
                           |
                           |    $simpleName(@Nonnull ${enumValueType.first} ${enumValueType.second}Value) {
                           |        this.${enumValueType.second}Value = ${enumValueType.second}Value;
                           |    }
                           |
                           |    public static $simpleName parse${enumValueType.first}(${enumValueType.first} value) {
                           |        switch (value) {
                           |            ${parserCases.indentWithMargin(3)}
                           |            default:
                           |                throw new UnsupportedOperationException("Unsupported value " + value);
                           |        }
                           |    }
                           |
                           |    public static ${enumValueType.first} writeString($simpleName value) {
                           |        return value.${enumValueType.second}Value;
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

    private fun toVariableType(format: String?, any: Any): Any {
        if (format == null || format == "string") {
            return "\"$any\""
        }
        return any
    }

    private fun getJavaTypeName(format: String?): Pair<String, String> {
        if (format == "number") {
            return Pair("Integer", "int")
        }
        return Pair("String", "str")
    }
}
