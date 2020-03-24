package jsm;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapParser<T> implements NonBlockingParser<Map<String, T>> {

    private ObjectParserState objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
    private java.lang.String currentField;
    private Map<String, T> items;
    private final NonBlockingParser<T> itemParser;

    public MapParser(NonBlockingParser<T> itemParser) {
        this.itemParser = itemParser;
    }

    @Override
    public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
        while (jsonParser.currentToken() == null || jsonParser.currentToken() != JsonToken.NOT_AVAILABLE) {
            JsonToken token;
            switch (objectParserState) {
                case PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL:
                    if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                        switch (token) {
                            case START_OBJECT:
                                items = new HashMap<>();
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
                    if (itemParser.parseNext(jsonParser)) {
                        ParseResult<T> parseResult = itemParser.build();
                        if (parseResult == ParseResult.NULL_VALUE) {
                            items.put(currentField, null);
                            objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                        } else if (parseResult == ParseResult.END_ARRAY) {
                            objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                            return true;
                        } else {
                            T value = parseResult.getValue();
                            items.put(currentField, value);
                            objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("unexpected state " + objectParserState);
            }
        }
        return false;
    }

    @Override
    public ParseResult<Map<String, T>> build() {
        switch (objectParserState) {
            case FINISHED_VALUE:
                objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                return new ParseResult.Value<>(Collections.unmodifiableMap(items));
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
