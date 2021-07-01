package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

class EnumConverterMatcher(private val dtoBasePackage: String, private val routesBasePackage: String) : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "enum"
        override fun provide(dtoPackage: String, routesPackage: String) =
            EnumConverterMatcher(dtoPackage, routesPackage)
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return if (jsonSchema.type is JsonType.Scalar.STRING && jsonSchema.enum() != null) object : ConverterWriter {
            private val converterType = toJavaClassName(routesBasePackage, jsonSchema, "converter")
            override val jsonSchema = jsonSchema
            override fun valueType() = toJavaClassName(dtoBasePackage, jsonSchema)
            override fun parseExpression(valueExpression: String, localVariableSuffix: Int) =
                "${converterType}.parse($valueExpression)"
            override fun writeExpression(jsonGeneratorName: String, valueExpression: String, localVariableSuffix: Int) =
                "${converterType}.write($jsonGeneratorName, $valueExpression)"
            override fun stringParseExpression(valueExpression: String) =
                "${converterType}.parseString($valueExpression)"
            override fun stringWriteExpression(valueExpression: String) =
                "${converterType}.writeString($valueExpression)"
            override fun generate() : ConverterWriter.Result {
                val outputFiles = listOf(
                    toDtoOutputFile(jsonSchema),
                    toConverterOutputFile(jsonSchema)
                )
                return ConverterWriter.Result(outputFiles, emptyList())
            }
        }
        else null
    }

    private fun toDtoOutputFile(jsonSchema: JsonSchema): OutputFile {
        val className = toJavaClassName(dtoBasePackage, jsonSchema)
        val dtoFilePath = getFilePath(className)

        val enumValues = jsonSchema.enum() ?: error("enum values should be defined")
        val valueDeclarations = enumValues.joinToString(",\n") { enumValue ->
            """${toUpperSnakeCase(enumValue)}("$enumValue")"""
        }

        val dtoContent =
            """|package ${getPackage(className)};
               |
               |import javax.annotation.Nonnull;
               |
               |${javaDoc(jsonSchema)}
               |public enum ${getSimpleName(className)} {
               |    ${valueDeclarations.indentWithMargin(1)};
               |
               |    @Nonnull
               |    public final String strValue;
               |
               |    ${getSimpleName(className)}(String strValue) {
               |        this.strValue = strValue;
               |    }
               |}
               |
            """.trimMargin()
        return OutputFile(dtoFilePath, dtoContent, OutputFileType.DTO)
    }

    private fun toConverterOutputFile(jsonSchema: JsonSchema): OutputFile {
        val dtoClassName = toJavaClassName(dtoBasePackage, jsonSchema)
        val converterClassName = toJavaClassName(routesBasePackage, jsonSchema, "converter")
        val converterFilePath = getFilePath(converterClassName)
        val converterSimpleName = getSimpleName(converterClassName)
        val switchCases = jsonSchema.enum()!!.map { enumValue ->
            """|case "$enumValue":
               |    return $dtoClassName.${toUpperSnakeCase(enumValue)};
            """.trimMargin()
        }
        val converterContent =
            """|package ${getPackage(converterClassName)};
               |
               |import com.fasterxml.jackson.core.JsonGenerator;
               |import com.fasterxml.jackson.databind.JsonNode;
               |import com.fasterxml.jackson.databind.node.JsonNodeType;
               |import io.github.fomin.oasgen.ConverterUtils;
               |import io.github.fomin.oasgen.EnumConverter;
               |import io.github.fomin.oasgen.ValidationError;
               |import io.github.fomin.oasgen.ValidationException;
               |import java.io.IOException;
               |import java.util.Collections;
               |import java.util.List;
               |
               |public final class $converterSimpleName {
               |
               |    private $converterSimpleName() {
               |    }
               |
               |    public static $dtoClassName parse(JsonNode jsonNode) {
               |        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
               |        String textValue = jsonNode.textValue();
               |        return parseString(textValue);
               |    }
               |
               |    public static $dtoClassName parseString(String value) {
               |        switch (value) {
               |            ${switchCases.indentWithMargin(3)}
               |            default:
               |                throw new ValidationException(new ValidationError.StringValue(
               |                        EnumConverter.NOT_IN_ENUM_ERROR_CODE, value
               |                ));
               |        }
               |    }
               |
               |    public static List<? extends ValidationError> write(
               |            JsonGenerator jsonGenerator,
               |            $dtoClassName value
               |    ) throws IOException {
               |        jsonGenerator.writeString(value.strValue);
               |        return Collections.emptyList();
               |    }
               |
               |    public static String writeString($dtoClassName value) {
               |        return value.strValue;
               |    }
               |}
               |""".trimMargin()
        return OutputFile(converterFilePath, converterContent, OutputFileType.DTO)
    }
}
