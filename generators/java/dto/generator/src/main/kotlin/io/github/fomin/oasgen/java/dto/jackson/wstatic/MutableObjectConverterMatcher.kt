package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

class MutableObjectConverterMatcher(val dtoBasePackage: String, val routesBasePackage: String) : ConverterMatcher {
    class Provider : ConverterMatcherProvider {
        override val id = "mutable-object"
        override fun provide(dtoPackage: String, routesPackage: String) =
            MutableObjectConverterMatcher(dtoPackage, routesPackage)
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.OBJECT -> object : ConverterWriter {
                private val converterType = toJavaClassName(routesBasePackage, jsonSchema, "converter")
                override val jsonSchema = jsonSchema
                override fun valueType() = toJavaClassName(dtoBasePackage, jsonSchema)
                override fun parseExpression(valueExpression: String, localVariableSuffix: Int) =
                    "${converterType}.parse($valueExpression)"
                override fun writeExpression(
                    jsonGeneratorName: String,
                    valueExpression: String,
                    localVariableSuffix: Int
                ) =
                    "${converterType}.write($jsonGeneratorName, $valueExpression)"
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
                        ObjectConverterMatcher.JavaProperty(
                                propertyName,
                                toVariableName(propertyName),
                                propertyType,
                                propertySchema,
                                nullAnnotation
                        )
                    }

                    val fieldDeclarations = javaProperties.map { javaProperty ->
                        """|${javaDoc(javaProperty.jsonSchema)}
                           |public ${javaProperty.type} ${javaProperty.variableName};""".trimMargin()
                    }

                    val accessorDeclarations = javaProperties.map { javaProperty ->
                        val variableName = javaProperty.variableName
                        """|public ${javaProperty.type} get${upperFirst(variableName)}() {
                           |    return this.$variableName;
                           |}
                           |
                           |public void set${upperFirst(variableName)}(${javaProperty.type} $variableName) {
                           |    this.$variableName = $variableName;
                           |}
                           |
                        """.trimMargin()
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
                       |public final class $simpleName implements java.io.Serializable {
                       |
                       |    ${fieldDeclarations.indentWithMargin(1)}
                       |
                       |    public $simpleName() {
                       |    }
                       |
                       |    public $simpleName(
                       |            ${constructorArgs.indentWithMargin(3)}
                       |    ) {
                       |        ${constructorChecks.indentWithMargin(2)}
                       |        ${constructorAssignments.indentWithMargin(2)}
                       |    }
                       |
                       |    ${accessorDeclarations.indentWithMargin(1)}
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
                    val converterOutputFile = ObjectConverterMatcher.ConverterClassWriter(converterRegistry, dtoBasePackage, routesBasePackage)
                        .write(jsonSchema, javaProperties)
                    val dtoOutputFile = OutputFile(filePath, content, OutputFileType.DTO)
                    val outputFiles = listOf(dtoOutputFile, converterOutputFile)
                    return ConverterWriter.Result(outputFiles, propertySchemas)
                }
            }
            else -> null
        }
    }
}
