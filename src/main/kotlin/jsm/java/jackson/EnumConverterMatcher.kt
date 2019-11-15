package jsm.java.jackson

import jsm.JsonSchema
import jsm.JsonType
import jsm.OutputFile
import jsm.indentWithMargin
import jsm.java.*

class EnumConverterMatcher(private val basePackage: String) : ConverterMatcher {
    override fun match(jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.enum() != null) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType(converterRegistry: ConverterRegistry) = toJavaClassName(basePackage, jsonSchema)
            override fun parserCreateExpression(converterRegistry: ConverterRegistry) = "${valueType(converterRegistry)}.createParser()"
            override fun writerCreateExpression(converterRegistry: ConverterRegistry) = "${valueType(converterRegistry)}.WRITER"
            override fun generate(converterRegistry: ConverterRegistry) : ConverterWriter.Result {
                val className = valueType(converterRegistry)
                val filePath = getFilePath(className)

                val classJavaDoc = jsonSchema.title?.let { title ->
                    """|/**
                           | * $title
                           | */""".trimMargin()
                } ?: ""

                val enumValues = jsonSchema.enum() ?: error("enum values should be defined")
                val valueDeclarations = enumValues.joinToString(",\n") { enumValue ->
                    """${toUpperSnakeCase(enumValue)}("$enumValue")"""
                }

                val parserCases = enumValues.map { enumValue ->
                    """|case "$enumValue":
                       |    return ${toUpperSnakeCase(enumValue)};""".trimMargin()
                }

                val content = """
                       |package ${getPackage(className)};
                       |
                       |$classJavaDoc
                       |public enum ${getSimpleName(className)} {
                       |    ${valueDeclarations.indentWithMargin(1)};
                       |
                       |    public final String strValue;
                       |
                       |    ${getSimpleName(className)}(String strValue) {
                       |        this.strValue = strValue;
                       |    }
                       |
                       |    public static jsm.NonBlockingParser<$className> createParser() {
                       |        return new jsm.ScalarParser<>(
                       |                token -> token == com.fasterxml.jackson.core.JsonToken.VALUE_STRING,
                       |                jsonParser -> {
                       |                    String value = jsonParser.getText();
                       |                    switch (value) {
                       |                        ${parserCases.indentWithMargin(6)}
                       |                        default:
                       |                            throw new UnsupportedOperationException("Unsupported value " + value);
                       |                    }
                       |                }
                       |        );
                       |    }
                       |
                       |    public static final jsm.Writer<$className> WRITER =
                       |            (jsonGenerator, value) -> jsonGenerator.writeString(value.strValue);
                       |
                       |}
                       |
                    """.trimMargin()

                return ConverterWriter.Result(OutputFile(filePath, content), emptyList())

            }
        }
        else null
    }
}
