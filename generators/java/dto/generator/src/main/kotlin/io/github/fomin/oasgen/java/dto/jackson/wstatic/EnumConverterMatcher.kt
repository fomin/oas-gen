package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.JsonSchema
import io.github.fomin.oasgen.JsonType
import io.github.fomin.oasgen.OutputFile
import io.github.fomin.oasgen.indentWithMargin
import io.github.fomin.oasgen.java.*

class EnumConverterMatcher(private val basePackage: String) : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "enum"
        override fun provide(basePackage: String) = EnumConverterMatcher(basePackage)
    }


    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.enum() != null) object : ConverterWriter {
            override val jsonSchema = jsonSchema
            override fun valueType() = toJavaClassName(basePackage, jsonSchema)
            override fun parserCreateExpression() = "${valueType()}.createParser()"
            override fun writerCreateExpression() = "${valueType()}.WRITER"
            override fun generate() : ConverterWriter.Result {
                val className = valueType()
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
                       |import javax.annotation.Nonnull;
                       |
                       |$classJavaDoc
                       |public enum ${getSimpleName(className)} {
                       |    ${valueDeclarations.indentWithMargin(1)};
                       |
                       |    @Nonnull
                       |    public final String strValue;
                       |
                       |    ${getSimpleName(className)}(@Nonnull String strValue) {
                       |        this.strValue = strValue;
                       |    }
                       |
                       |    public static io.github.fomin.oasgen.NonBlockingParser<$className> createParser() {
                       |        return new io.github.fomin.oasgen.ScalarParser<>(
                       |                token -> token == com.fasterxml.jackson.core.JsonToken.VALUE_STRING,
                       |                jsonParser -> of(jsonParser.getText())
                       |        );
                       |    }
                       |
                       |    public static final io.github.fomin.oasgen.Writer<$className> WRITER =
                       |            (jsonGenerator, value) -> jsonGenerator.writeString(value.strValue);
                       |
                       |    public static ${getSimpleName(className)} of(String value) {
                       |        switch (value) {
                       |            ${parserCases.indentWithMargin(3)}
                       |            default:
                       |                throw new UnsupportedOperationException("Unsupported value " + value);
                       |        }
                       |    }
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
