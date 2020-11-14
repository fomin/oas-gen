package com.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import io.github.fomin.oasgen.NonBlockingParser;
import io.github.fomin.oasgen.ObjectParserState;
import io.github.fomin.oasgen.ParseResult;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Component item
 */
public final class ComponentItem {



    public ComponentItem(

    ) {


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.ComponentItem other = (com.example.ComponentItem) o;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(

        );
    }

    @Override
    public String toString() {
        return "ComponentItem{" +

                '}';
    }

    public static class Parser implements NonBlockingParser<com.example.ComponentItem> {

        private ObjectParserState objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
        private java.lang.String currentField;



        @Override
        public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
            while (true) {
                JsonToken token;
                switch (objectParserState) {
                    case PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL:
                        if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                            switch (token) {
                                case START_OBJECT:

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

                    default:
                        throw new RuntimeException("unexpected state " + objectParserState);
                }
            }
        }

        @Override
        public ParseResult<com.example.ComponentItem> build() {
            switch (objectParserState) {
                case FINISHED_VALUE:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return new ParseResult.Value<>(new com.example.ComponentItem());
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

    public static class Writer implements io.github.fomin.oasgen.Writer<com.example.ComponentItem> {
        public static final Writer INSTANCE = new Writer();


        @Override
        public void write(JsonGenerator jsonGenerator, com.example.ComponentItem value) throws IOException {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeEndObject();
        }
    }
}
