package com.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import io.github.fomin.oasgen.NonBlockingParser;
import io.github.fomin.oasgen.ObjectParserState;
import io.github.fomin.oasgen.ParseResult;
import io.github.fomin.oasgen.SkipValueParser;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class Dto {


    @Nullable
    public final java.lang.String property1;

    public Dto(
            @Nullable java.lang.String property1
    ) {

        this.property1 = property1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.Dto other = (com.example.Dto) o;
        return Objects.equals(property1, other.property1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                property1
        );
    }

    @Override
    public String toString() {
        return "Dto{" +
                "property1='" + property1 + '\'' +
                '}';
    }

    public static class Parser implements NonBlockingParser<com.example.Dto> {

        private ObjectParserState objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
        private java.lang.String currentField;
        private java.lang.String p0; // property1
        private final io.github.fomin.oasgen.SkipValueParser skipValueParser = new io.github.fomin.oasgen.SkipValueParser();
        private final io.github.fomin.oasgen.NonBlockingParser<java.lang.String> parser0 = io.github.fomin.oasgen.StringConverter.createParser();

        @Override
        public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
            while (true) {
                JsonToken token;
                switch (objectParserState) {
                    case PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL:
                        if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                            switch (token) {
                                case START_OBJECT:
                                    this.p0 = null;
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
                        } else {
                            return false;
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
                        } else {
                            return false;
                        }
                        break;
                    case PARSE_FIELD_VALUE:
                        switch (currentField) {
                            case "property1":
                                if (parser0.parseNext(jsonParser)) {
                                    ParseResult<java.lang.String> parseResult = parser0.build();
                                    this.p0 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                } else {
                                    return false;
                                }
                                break;
                            default:
                                if (skipValueParser.parseNext(jsonParser)) {
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                } else {
                                    return false;
                                }
                        }
                        break;
                    default:
                        throw new RuntimeException("unexpected state " + objectParserState);
                }
            }
        }

        @Override
        public ParseResult<com.example.Dto> build() {
            switch (objectParserState) {
                case FINISHED_VALUE:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return new ParseResult.Value<>(new com.example.Dto(this.p0));
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

    public static class Writer implements io.github.fomin.oasgen.Writer<com.example.Dto> {
        public static final Writer INSTANCE = new Writer();
        private static final io.github.fomin.oasgen.Writer<java.lang.String> WRITER_0 = io.github.fomin.oasgen.StringConverter.WRITER;

        @Override
        public void write(JsonGenerator jsonGenerator, com.example.Dto value) throws IOException {
            jsonGenerator.writeStartObject();
            if (value.property1 != null) {
                jsonGenerator.writeFieldName("property1");
                WRITER_0.write(jsonGenerator, value.property1);
            }
            jsonGenerator.writeEndObject();
        }
    }
}
