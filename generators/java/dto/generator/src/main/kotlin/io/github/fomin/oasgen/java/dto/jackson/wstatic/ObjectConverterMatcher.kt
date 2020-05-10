package io.github.fomin.oasgen.java.dto.jackson.wstatic

import io.github.fomin.oasgen.*
import io.github.fomin.oasgen.java.*

data class ClassMember(val content: String, val importedClasses: List<String>)

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
            val internalVariableName: String,
            val nullAnnotation: String
    )

    class JacksonParserWriter(private val converterRegistry: ConverterRegistry) {
        private data class ParserPair(
                val valueType: String,
                val parserCreateExpression: String
        ) : Comparable<ParserPair> {
            override fun compareTo(other: ParserPair) =
                    this.parserCreateExpression.compareTo(other.parserCreateExpression)
        }

        fun write(
                jsonSchema: JsonSchema,
                dtoClassName: String
        ): ClassMember {
            val importedClasses = listOf(
                    "com.fasterxml.jackson.core.JsonGenerator",
                    "com.fasterxml.jackson.core.JsonToken",
                    "com.fasterxml.jackson.core.json.async.NonBlockingJsonParser",
                    "java.io.IOException",
                    "io.github.fomin.oasgen.NonBlockingParser",
                    "io.github.fomin.oasgen.ObjectParserState",
                    "io.github.fomin.oasgen.ParseResult",
                    "javax.annotation.Nonnull",
                    "javax.annotation.Nullable"
            )

            val jointProperties = jsonSchema.jointProperties()
            val builderPropertyDeclarations = jointProperties.entries.mapIndexed { index, (propertyName, propertySchema) ->
                val converterWriter = converterRegistry[propertySchema]
                "private ${converterWriter.valueType()} p$index; // $propertyName"
            }

            val parserPairs = jointProperties.map { (_, propertySchema) ->
                val converterWriter = converterRegistry[propertySchema]
                ParserPair(converterWriter.valueType(), converterWriter.parserCreateExpression())
            }.toSortedSet()

            val propertyParserDeclarations = parserPairs.mapIndexed { index, parserPair ->
                "private final io.github.fomin.oasgen.NonBlockingParser<${parserPair.valueType}> parser$index = ${parserPair.parserCreateExpression};"
            }

            val parseValueCases = jointProperties.entries.mapIndexed { index, (propertyName, propertySchema) ->
                val converterWriter = converterRegistry[propertySchema]

                val parserIndex = parserPairs.indexOfFirst { it.parserCreateExpression == converterWriter.parserCreateExpression() }
                """|case "$propertyName":
               |    if (parser$parserIndex.parseNext(jsonParser)) {
               |        ParseResult<${converterWriter.valueType()}> parseResult = parser${parserIndex}.build();
               |        this.p${index} = parseResult.getValue();
               |        objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
               |    }
               |    break;
            """.trimMargin()
            }

            val constructorArgs = jointProperties.entries
                    .mapIndexed { index, _ -> index }
                    .joinToString(", ") { "this.p${it}" }

            val resetFieldExpressions = jointProperties.entries.mapIndexed { index, _ -> "this.p$index = null;" }
            val parseFieldValueCase = when (parseValueCases.isNotEmpty()) {
                true ->
                    """|case PARSE_FIELD_VALUE:
                       |    switch (currentField) {
                       |        ${parseValueCases.indentWithMargin(2)}
                       |        default:
                       |            throw new UnsupportedOperationException("Unexpected field " + currentField);
                       |    }
                       |    break;
                       |
                    """.trimMargin()
                false -> ""
            }

            val content = """
               |public static class Parser implements NonBlockingParser<$dtoClassName> {
               |
               |    private ObjectParserState objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
               |    private java.lang.String currentField;
               |    ${builderPropertyDeclarations.indentWithMargin(1)}
               |    ${propertyParserDeclarations.indentWithMargin(1)}
               |
               |    @Override
               |    public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
               |        while (jsonParser.currentToken() == null || jsonParser.currentToken() != JsonToken.NOT_AVAILABLE) {
               |            JsonToken token;
               |            switch (objectParserState) {
               |                case PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL:
               |                    if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
               |                        switch (token) {
               |                            case START_OBJECT:
               |                                ${resetFieldExpressions.indentWithMargin(8)}
               |                                objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
               |                                break;
               |                            case END_ARRAY:
               |                                objectParserState = ObjectParserState.FINISHED_ARRAY;
               |                                return true;
               |                            case VALUE_NULL:
               |                                objectParserState = ObjectParserState.FINISHED_NULL;
               |                                return true;
               |                            default:
               |                                throw new RuntimeException("Unexpected token " + token);
               |                        }
               |                    }
               |                    break;
               |                case PARSE_FIELD_NAME_OR_END_OBJECT:
               |                    if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
               |                        switch (token) {
               |                            case FIELD_NAME:
               |                                currentField = jsonParser.getCurrentName();
               |                                objectParserState = ObjectParserState.PARSE_FIELD_VALUE;
               |                                break;
               |                            case END_OBJECT:
               |                                objectParserState = ObjectParserState.FINISHED_VALUE;
               |                                return true;
               |                            default:
               |                                throw new RuntimeException("Unexpected token " + token);
               |                        }
               |                    }
               |                    break;
               |                ${parseFieldValueCase.indentWithMargin(4)}
               |                default:
               |                    throw new RuntimeException("unexpected state " + objectParserState);
               |            }
               |        }
               |        return false;
               |    }
               |
               |    @Override
               |    public ParseResult<$dtoClassName> build() {
               |        switch (objectParserState) {
               |            case FINISHED_VALUE:
               |                objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
               |                return new ParseResult.Value<>(new $dtoClassName($constructorArgs));
               |            case FINISHED_ARRAY:
               |                objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
               |                return ParseResult.endArray();
               |            case FINISHED_NULL:
               |                objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
               |                return ParseResult.nullValue();
               |            default:
               |                throw new IllegalStateException("Parsing is not completed");
               |        }
               |    }
               |
               |}
               |
            """.trimMargin()
            return ClassMember(content, importedClasses)
        }
    }

    class JacksonWriterWriter(private val converterRegistry: ConverterRegistry) {
        private data class WriterPair(
                val valueType: String,
                val writerCreateExpression: String
        ) : Comparable<WriterPair> {
            override fun compareTo(other: WriterPair) =
                    this.writerCreateExpression.compareTo(other.writerCreateExpression)
        }

        fun write(
                jsonSchema: JsonSchema,
                dtoClassName: String
        ): ClassMember {
            val importedClasses = listOf(
                    "com.fasterxml.jackson.core.JsonGenerator"
            )

            val jointProperties = jsonSchema.jointProperties()
            val writerPairs = jointProperties.map { (_, propertySchema) ->
                val converterWriter = converterRegistry[propertySchema]
                WriterPair(converterWriter.valueType(), converterWriter.writerCreateExpression())
            }.toSortedSet()

            val writerDeclarations = writerPairs.mapIndexed { index, parserPair ->
                "private static final io.github.fomin.oasgen.Writer<${parserPair.valueType}> WRITER_$index = ${parserPair.writerCreateExpression};"
            }

            val propertyBlocks = jointProperties.map { (propertyName, propertySchema) ->
                val converterWriter = converterRegistry[propertySchema]

                val fieldName = toVariableName(propertyName)
                val writerIndex = writerPairs.indexOfFirst { it.writerCreateExpression == converterWriter.writerCreateExpression() }
                """|if (value.$fieldName != null) {
               |    jsonGenerator.writeFieldName("$propertyName");
               |    WRITER_$writerIndex.write(jsonGenerator, value.$fieldName);
               |}
            """.trimMargin()
            }

            val content = """
           |public static class Writer implements io.github.fomin.oasgen.Writer<$dtoClassName> {
           |    public static final Writer INSTANCE = new Writer();
           |    ${writerDeclarations.indentWithMargin(1)}
           |
           |    @Override
           |    public void write(JsonGenerator jsonGenerator, $dtoClassName value) throws IOException {
           |        jsonGenerator.writeStartObject();
           |        ${propertyBlocks.indentWithMargin(2)}
           |        jsonGenerator.writeEndObject();
           |    }
           |}
           |
        """.trimMargin()
            return ClassMember(content, importedClasses)
        }
    }

    override fun match(converterRegistry: ConverterRegistry, jsonSchema: JsonSchema): ConverterWriter? {
        return when (jsonSchema.type) {
            is JsonType.OBJECT -> object : ConverterWriter {
                override val jsonSchema = jsonSchema
                override fun valueType() = toJavaClassName(basePackage, jsonSchema)
                override fun parserCreateExpression() = "new ${valueType()}.Parser()"
                override fun writerCreateExpression() = "${valueType()}.Writer.INSTANCE"
                override fun generate(): ConverterWriter.Result {
                    val filePath = getFilePath(valueType())

                    val jointProperties = jsonSchema.jointProperties()
                    val javaProperties = jointProperties.entries.mapIndexed { index, (propertyName, propertySchema) ->
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
                                "p$index",
                                nullAnnotation
                        )
                    }

                    val fieldDeclarations = javaProperties.map { javaProperty ->
                        val javaDoc = javaProperty.jsonSchema.title?.let { title ->
                            """|/**
                               | * $title
                               | */""".trimMargin()
                        } ?: ""
                        """|$javaDoc
                           |${javaProperty.nullAnnotation}
                           |public final ${javaProperty.type} ${javaProperty.variableName};""".trimMargin()
                    }

                    val constructorArgs = javaProperties.joinToString(",\n") {
                        "${it.nullAnnotation} ${it.type} ${it.variableName}"
                    }
                    val constructorAssignments = javaProperties.map { javaProperty ->
                        "this.${javaProperty.variableName} = ${javaProperty.variableName};"
                    }

                    val jacksonParserWriter = JacksonParserWriter(converterRegistry)
                    val (parserContent, parserImports) = jacksonParserWriter.write(jsonSchema, valueType())
                    val jacksonWriterWriter = JacksonWriterWriter(converterRegistry)
                    val (writerContent, writerImports) = jacksonWriterWriter.write(jsonSchema, valueType())
                    val importDeclarations = (parserImports + writerImports).map { "import $it;" }.toSortedSet()
                    val classJavaDoc = jsonSchema.title?.let { title ->
                        """|/**
                           | * $title
                           | */""".trimMargin()
                    } ?: ""

                    val content = """
                       |package ${getPackage(valueType())};
                       |
                       |${importDeclarations.indentWithMargin(0)}
                       |
                       |$classJavaDoc
                       |public final class ${getSimpleName(valueType())} {
                       |
                       |    ${fieldDeclarations.indentWithMargin(1)}
                       |
                       |    public ${getSimpleName(valueType())}(
                       |            ${constructorArgs.indentWithMargin(3)}
                       |    ) {
                       |        ${constructorAssignments.indentWithMargin(2)}
                       |    }
                       |
                       |    ${parserContent.indentWithMargin(1)}
                       |
                       |    ${writerContent.indentWithMargin(1)}
                       |}
                       |
                    """.trimMargin()

                    val propertySchemas = jointProperties.map { it.value }
                    return ConverterWriter.Result(OutputFile(filePath, content), propertySchemas)
                }
            }
            else -> null
        }
    }
}
