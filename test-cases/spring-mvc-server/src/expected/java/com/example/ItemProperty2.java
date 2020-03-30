package com.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import io.github.fomin.oasgen.NonBlockingParser;
import io.github.fomin.oasgen.ObjectParserState;
import io.github.fomin.oasgen.ParseResult;
import java.io.IOException;

/**
 * Property 2
 */
public final class ItemProperty2 {

    /**
     * Common property 1
     */
    public final java.lang.String commonProperty1;
    /**
     * Property 21
     */
    public final java.lang.String property21;
    /**
     * Property 22
     */
    public final com.example.ItemProperty2Property22 property22;

    public ItemProperty2(
            java.lang.String commonProperty1,
            java.lang.String property21,
            com.example.ItemProperty2Property22 property22
    ) {
        this.commonProperty1 = commonProperty1;
        this.property21 = property21;
        this.property22 = property22;
    }

    public static class Parser implements NonBlockingParser<com.example.ItemProperty2> {

        private ObjectParserState objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
        private java.lang.String currentField;
        private java.lang.String p0; // commonProperty1
        private java.lang.String p1; // property21
        private com.example.ItemProperty2Property22 p2; // property22
        private final io.github.fomin.oasgen.NonBlockingParser<com.example.ItemProperty2Property22> parser0 = com.example.ItemProperty2Property22.createParser();
        private final io.github.fomin.oasgen.NonBlockingParser<java.lang.String> parser1 = io.github.fomin.oasgen.ScalarParser.createStringParser();

        @Override
        public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
            while (jsonParser.currentToken() == null || jsonParser.currentToken() != JsonToken.NOT_AVAILABLE) {
                JsonToken token;
                switch (objectParserState) {
                    case PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL:
                        if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                            switch (token) {
                                case START_OBJECT:
                                    this.p0 = null;
                                    this.p1 = null;
                                    this.p2 = null;
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                    break;
                                case END_ARRAY:
                                    objectParserState = ObjectParserState.FINISHED_ARRAY;
                                    return true;
                                case VALUE_NULL:
                                    objectParserState = ObjectParserState.FINISHED_NULL;
                                    return true;
                                default:
                                    throw new RuntimeException("Unexpected token " + token);
                            }
                        }
                        break;
                    case PARSE_FIELD_NAME_OR_END_OBJECT:
                        if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                            switch (token) {
                                case FIELD_NAME:
                                    currentField = jsonParser.getCurrentName();
                                    objectParserState = ObjectParserState.PARSE_FIELD_VALUE;
                                    break;
                                case END_OBJECT:
                                    objectParserState = ObjectParserState.FINISHED_VALUE;
                                    return true;
                                default:
                                    throw new RuntimeException("Unexpected token " + token);
                            }
                        }
                        break;
                    case PARSE_FIELD_VALUE:
                        switch (currentField) {
                            case "commonProperty1":
                                if (parser1.parseNext(jsonParser)) {
                                    ParseResult<java.lang.String> parseResult = parser1.build();
                                    this.p0 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "property21":
                                if (parser1.parseNext(jsonParser)) {
                                    ParseResult<java.lang.String> parseResult = parser1.build();
                                    this.p1 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "property22":
                                if (parser0.parseNext(jsonParser)) {
                                    ParseResult<com.example.ItemProperty2Property22> parseResult = parser0.build();
                                    this.p2 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            default:
                                throw new UnsupportedOperationException("Unexpected field " + currentField);
                        }
                        break;
                    default:
                        throw new RuntimeException("unexpected state " + objectParserState);
                }
            }
            return false;
        }

        @Override
        public ParseResult<com.example.ItemProperty2> build() {
            switch (objectParserState) {
                case FINISHED_VALUE:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return new ParseResult.Value<>(new com.example.ItemProperty2(this.p0, this.p1, this.p2));
                case FINISHED_ARRAY:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return ParseResult.endArray();
                case FINISHED_NULL:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return ParseResult.nullValue();
                default:
                    throw new IllegalStateException("Parsing is not completed");
            }
        }

    }

    public static class Writer implements io.github.fomin.oasgen.Writer<com.example.ItemProperty2> {
        public static final Writer INSTANCE = new Writer();
        private static final io.github.fomin.oasgen.Writer<com.example.ItemProperty2Property22> WRITER_0 = com.example.ItemProperty2Property22.WRITER;
        private static final io.github.fomin.oasgen.Writer<java.lang.String> WRITER_1 = io.github.fomin.oasgen.ScalarWriter.STRING_WRITER;

        @Override
        public void write(JsonGenerator jsonGenerator, com.example.ItemProperty2 value) throws IOException {
            jsonGenerator.writeStartObject();
            if (value.commonProperty1 != null) {
                jsonGenerator.writeFieldName("commonProperty1");
                WRITER_1.write(jsonGenerator, value.commonProperty1);
            }
            if (value.property21 != null) {
                jsonGenerator.writeFieldName("property21");
                WRITER_1.write(jsonGenerator, value.property21);
            }
            if (value.property22 != null) {
                jsonGenerator.writeFieldName("property22");
                WRITER_0.write(jsonGenerator, value.property22);
            }
            jsonGenerator.writeEndObject();
        }
    }
}
