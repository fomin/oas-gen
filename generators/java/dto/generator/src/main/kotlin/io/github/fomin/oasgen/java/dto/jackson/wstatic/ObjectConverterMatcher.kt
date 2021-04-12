package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

class ObjectConverterMatcher(val basePackage: String) : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "object"
        override fun provide(basePackage: String) = ObjectConverterMatcher(basePackage)
    }

    data class JavaProperty(
            val name: String,
            val variableName: String,
            val type: String,
            val jsonSchema: JsonSchema,
            val nullAnnotation: String
    )

    class ConverterClassWriter(
        private val converterRegistry: ConverterRegistry,
        private val basePackage: String
    ) {
        fun write(jsonSchema: JsonSchema, javaProperties: List<JavaProperty>): OutputFile {
            val className = toJavaClassName(basePackage, jsonSchema)
            val converterClassName = "${className}Converter"
            val converterSimpleName = getSimpleName(converterClassName)
            val converterFilePath = getFilePath(converterClassName)

            val propertyVariableDeclarations = javaProperties.mapIndexed { index, javaProperty ->
                "${javaProperty.type} p${index} = null; // ${javaProperty.name}"
            }

            val switchCases = javaProperties.mapIndexed { index, javaProperty ->
                """|case "${javaProperty.name}":
                   |    p$index = ${converterRegistry[javaProperty.jsonSchema].parseExpression("value")};
                   |    break;
                """.trimMargin()
            }

            val constructorArguments = javaProperties.mapIndexed { index, _ ->
                "p$index"
            }.joinToString(", ")

            val propertyWriteStatements = javaProperties.map { javaProperty ->
                val variableName = javaProperty.variableName
                """|if (value.$variableName != null) {
                   |    jsonGenerator.writeFieldName("${javaProperty.name}");
                   |    List<? extends ValidationError> validationErrors = ${converterRegistry[javaProperty.jsonSchema].writeExpression("value.$variableName")};
                   |    if (!validationErrors.isEmpty()) {
                   |        if (errors == null) {
                   |            errors = new ArrayList<>();
                   |        }
                   |        errors.add(new ValidationError.ObjectFieldError(
                   |                "${javaProperty.name}",
                   |                validationErrors
                   |        ));
                   |    }
                   |}
                """.trimMargin()
            }

            val content =
                """|package $basePackage;
                   |
                   |import com.fasterxml.jackson.core.JsonGenerator;
                   |import com.fasterxml.jackson.databind.JsonNode;
                   |import com.fasterxml.jackson.databind.node.JsonNodeType;
                   |import io.github.fomin.oasgen.ConverterUtils;
                   |import io.github.fomin.oasgen.ValidationError;
                   |import io.github.fomin.oasgen.ValidationException;
                   |import java.io.IOException;
                   |import java.util.ArrayList;
                   |import java.util.Collections;
                   |import java.util.Iterator;
                   |import java.util.List;
                   |import java.util.Map;
                   |
                   |public final class $converterSimpleName {
                   |
                   |    private $converterSimpleName() {
                   |    }
                   |
                   |    public static $className parse(JsonNode jsonNode) {
                   |        ConverterUtils.checkNodeType(JsonNodeType.OBJECT, jsonNode);
                   |        ${propertyVariableDeclarations.indentWithMargin(2)}
                   |        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                   |        List<ValidationError.ObjectFieldError> errors = null;
                   |        while (fields.hasNext()) {
                   |            Map.Entry<String, JsonNode> entry = fields.next();
                   |            String key = entry.getKey();
                   |            JsonNode value = entry.getValue();
                   |            try {
                   |                switch (key) {
                   |                    ${switchCases.indentWithMargin(5)}
                   |                }
                   |            } catch (ValidationException e) {
                   |                if (errors == null) {
                   |                    errors = new ArrayList<>();
                   |                }
                   |                errors.add(new ValidationError.ObjectFieldError(
                   |                        key,
                   |                        e.validationErrors
                   |                ));
                   |            }
                   |        }
                   |        if (errors != null) {
                   |            throw new ValidationException(errors);
                   |        }
                   |        return new $className($constructorArguments);
                   |    }
                   |
                   |    public static List<? extends ValidationError> write(JsonGenerator jsonGenerator, $className value) throws IOException {
                   |        List<ValidationError.ObjectFieldError> errors = null;
                   |        jsonGenerator.writeStartObject();
                   |        ${propertyWriteStatements.indentWithMargin(2)}
                   |        jsonGenerator.writeEndObject();
                   |        if (errors != null) {
                   |            return errors;
                   |        } else {
                   |            return Collections.emptyList();
                   |        }
                   |    }
                   |}
                   |""".trimMargin()
            return OutputFile(converterFilePath, content)
        }
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.OBJECT -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = toJavaClassName(basePackage, jsonSchema)
                override fun parseExpression(valueExpression: String) =
                    "${valueType()}Converter.parse($valueExpression)"
                override fun writeExpression(valueExpression: String) =
                    "${valueType()}Converter.write(jsonGenerator, $valueExpression)"
                override fun stringParseExpression(valueExpression: String) = throw UnsupportedOperationException()
                override fun stringWriteExpression(valueExpression: String) = throw UnsupportedOperationException()
                override fun generate(): ConverterWriter.Result {
                    val filePath = getFilePath(valueType())

                    val jointProperties = jsonSchema.jointProperties()
                    val javaProperties = jointProperties.entries.map { (propertyName, propertySchema) ->
                        val propertyConverterWriter = converterRegistry[propertySchema]
                        val propertyType = propertyConverterWriter.valueType()
                        val nullAnnotation = when {
                            jsonSchema.jointRequired().contains(propertyName) -> "@Nonnull"
                            else -> "@Nullable"
                        }
                        JavaProperty(
                                propertyName,
                                toVariableName(propertyName),
                                propertyType,
                                propertySchema,
                                nullAnnotation
                        )
                    }

                    val fieldDeclarations = javaProperties.map { javaProperty ->
                        """|${javaDoc(javaProperty.jsonSchema)}
                           |${javaProperty.nullAnnotation}
                           |public final ${javaProperty.type} ${javaProperty.variableName};""".trimMargin()
                    }

                    val constructorArgs = javaProperties.joinToString(",\n") {
                        "${it.nullAnnotation} ${it.type} ${it.variableName}"
                    }
                    val constructorChecks = jointProperties.mapNotNull { (propertyName, _) ->
                        if (jsonSchema.required().contains(propertyName)) {
                            val variableName = toVariableName(propertyName)
                            """|if ($variableName == null) {
                               |    throw new NullPointerException("$variableName must be not null");
                               |}
                            """.trimMargin()
                        } else {
                            null
                        }
                    }
                    val constructorAssignments = javaProperties.map { javaProperty ->
                        "this.${javaProperty.variableName} = ${javaProperty.variableName};"
                    }

                    val equalsComparisons = if (jointProperties.isNotEmpty())
                        jointProperties.map { (propertyName, _) ->
                            val variableName = toVariableName(propertyName)
                            "Objects.equals($variableName, other.$variableName)"
                        }.joinToString(" &&\n")
                    else "true"

                    val hashArgs = jointProperties.map { (propertyName, _) ->
                        toVariableName(propertyName)
                    }.joinToString(",\n")

                    val toStringParts = jointProperties.entries.mapIndexed { index, (propertyName, _) ->
                        val variableName = toVariableName(propertyName)
                        """"${if (index == 0) "" else ", "}$variableName='" + $variableName + '\'' +"""
                    }


                    val simpleName = getSimpleName(valueType())
                    val content = """
                       |package ${getPackage(valueType())};
                       |
                       |import java.util.Objects;
                       |import javax.annotation.Nonnull;
                       |import javax.annotation.Nullable;
                       |
                       |${javaDoc(jsonSchema)}
                       |public final class $simpleName {
                       |
                       |    ${fieldDeclarations.indentWithMargin(1)}
                       |
                       |    public $simpleName(
                       |            ${constructorArgs.indentWithMargin(3)}
                       |    ) {
                       |        ${constructorChecks.indentWithMargin(2)}
                       |        ${constructorAssignments.indentWithMargin(2)}
                       |    }
                       |
                       |    @Override
                       |    public boolean equals(Object o) {
                       |        if (this == o) return true;
                       |        if (o == null || getClass() != o.getClass()) return false;
                       |        ${valueType()} other = (${valueType()}) o;
                       |        return ${equalsComparisons.indentWithMargin(4)};
                       |    }
                       |
                       |    @Override
                       |    public int hashCode() {
                       |        return Objects.hash(
                       |                ${hashArgs.indentWithMargin(4)}
                       |        );
                       |    }
                       |
                       |    @Override
                       |    public String toString() {
                       |        return "$simpleName{" +
                       |                ${toStringParts.indentWithMargin(4)}
                       |                '}';
                       |    }
                       |
                       |}
                       |
                    """.trimMargin().trimEndings()

                    val propertySchemas = jointProperties.map { it.value }
                    val converterOutputFile = ConverterClassWriter(converterRegistry, basePackage)
                        .write(jsonSchema, javaProperties)
                    val dtoOutputFile = OutputFile(filePath, content)
                    val outputFiles = listOf(dtoOutputFile, converterOutputFile)
                    return ConverterWriter.Result(outputFiles, propertySchemas)
                }
            }
            else -> null
        }
    }
}
