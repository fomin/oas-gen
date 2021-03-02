package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

class MutableObjectConverterMatcher(val basePackage: String) : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return when (jsonSchema.type) {
            is JsonType.OBJECT -> object : Converter {
                override val jsonSchema = jsonSchema
                override fun valueType() = toJavaClassName(basePackage, jsonSchema)
                override fun extraAnnotations(): String? = null
                override fun stringParseExpression(valueExpression: String) = throw UnsupportedOperationException()
                override fun stringWriteExpression(valueExpression: String) = throw UnsupportedOperationException()
                override fun output(): ConverterOutput {
                    val jointProperties = jsonSchema.jointProperties()
                    val simpleName = getSimpleName(valueType())

                    val propertyDeclarations = jointProperties.map { (propertyName, propertySchema) ->
                        val nullAnnotation = when (jsonSchema.required().contains(propertyName)) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        """|${javaDoc(propertySchema)}
                           |$nullAnnotation
                           |public ${converterRegistry[propertySchema].valueType()} ${toVariableName(propertyName)};
                           |
                        """.trimMargin()
                    }

                    val methodsDeclarations = jointProperties.map { (propertyName, propertySchema) ->
                        val nullAnnotation = when (jsonSchema.required().contains(propertyName)) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        """|$nullAnnotation
                           |@JsonProperty("$propertyName")
                           |public ${converterRegistry[propertySchema].valueType()} ${toMethodName("get", propertyName)}() {
                           |    return ${toVariableName(propertyName)};
                           |}
                           |
                           |@JsonProperty("$propertyName")
                           |public void ${toMethodName("set", propertyName)}($nullAnnotation ${converterRegistry[propertySchema].valueType()} ${toVariableName(propertyName)}) {
                           |    this.${toVariableName(propertyName)} = ${toVariableName(propertyName)};
                           |}
                           |
                        """.trimMargin()
                    }

                    val constructorArguments = jointProperties.map { (propertyName, propertySchema) ->
                        val converter = converterRegistry[propertySchema]
                        val propertyType = converter.valueType()
                        val variableName = toVariableName(propertyName)
                        val nullAnnotation = when (jsonSchema.required().contains(propertyName)) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        listOfNotNull(
                                nullAnnotation,
                                "@JsonProperty(\"$propertyName\")",
                                converter.extraAnnotations(),
                                propertyType,
                                variableName
                        ).joinToString(" ")
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

                    val constructorAssignments = jointProperties.map { (propertyName, _) ->
                        val variableName = toVariableName(propertyName)
                        """this.$variableName = $variableName;"""
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

                    val content =
                            """|package ${getPackage(valueType())};
                               |
                               |import com.fasterxml.jackson.annotation.JsonCreator;
                               |import com.fasterxml.jackson.annotation.JsonProperty;
                               |import javax.annotation.Nonnull;
                               |import javax.annotation.Nullable;
                               |import java.util.Objects;
                               |
                               |${javaDoc(jsonSchema)}
                               |public final class $simpleName {
                               |
                               |    ${propertyDeclarations.indentWithMargin(1)}
                               |
                               |    @JsonCreator
                               |    public $simpleName(
                               |            ${constructorArguments.joinToString(",\n").indentWithMargin(3)}
                               |    ) {
                               |        ${constructorChecks.indentWithMargin(2)}
                               |        ${constructorAssignments.indentWithMargin(2)}
                               |    }
                               |
                               |    ${methodsDeclarations.indentWithMargin(1)}
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
                               |}
                               |
                            """.trimMargin().trimEndings()
                    val filePath = getFilePath(valueType())
                    return ConverterOutput(OutputFile(filePath, content), jointProperties.map { it.value })
                }
            }
            else -> null
        }
    }
}
