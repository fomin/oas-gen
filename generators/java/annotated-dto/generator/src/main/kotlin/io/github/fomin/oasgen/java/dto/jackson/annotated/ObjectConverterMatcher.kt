package io.github.fomin.oasgen.java.dto.jackson.annotated

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

class ObjectConverterMatcher(val basePackage: String) : ConverterMatcher {
    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): Converter? {
        return when (jsonSchema.type) {
            is JsonType.OBJECT -> object : Converter {
                override val jsonSchema = jsonSchema
                override fun valueType() = toJavaClassName(basePackage, jsonSchema)
                override fun extraAnnotations(): String? = null
                override fun output(): ConverterOutput {
                    val jointProperties = jsonSchema.jointProperties()
                    val simpleName = getSimpleName(valueType())

                    val propertyDeclarations = jointProperties.map { (propertyName, propertySchema) ->
                        val nullAnnotation = when (jsonSchema.required().contains(propertyName)) {
                            true -> "@Nonnull"
                            false -> "@Nullable"
                        }
                        """|/**
                           | * ${propertySchema.title}
                           | */
                           |$nullAnnotation
                           |public final ${converterRegistry[propertySchema].valueType()} ${toVariableName(propertyName)};
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

                    val content =
                            """|package ${getPackage(valueType())};
                               |
                               |import com.fasterxml.jackson.annotation.JsonCreator;
                               |import com.fasterxml.jackson.annotation.JsonProperty;
                               |import javax.annotation.Nonnull;
                               |import javax.annotation.Nullable;
                               |
                               |/**
                               | * ${jsonSchema.title}
                               | */
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
