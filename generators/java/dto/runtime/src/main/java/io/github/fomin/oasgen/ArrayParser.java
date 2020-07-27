package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayParser<T> implements NonBlockingParser<List<T>> {

    private ArrayParserState arrayParserState = ArrayParserState.PARSE_START_ARRAY_OR_NULL_VALUE_OR_END_ARRAY;
    private List<T> items;
    private final NonBlockingParser<T> itemParser;

    public ArrayParser(NonBlockingParser<T> itemParser) {
        this.itemParser = itemParser;
    }

    @Override
    public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
        while (true) {
            JsonToken token;
            switch (arrayParserState) {
                case PARSE_START_ARRAY_OR_NULL_VALUE_OR_END_ARRAY:
                    if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                        switch (token) {
                            case START_ARRAY:
                                items = new ArrayList<>();
                                arrayParserState = ArrayParserState.PARSE_VALUE;
                                break;
                            case VALUE_NULL:
                                arrayParserState = ArrayParserState.FINISHED_NULL;
                                return true;
                            case END_ARRAY:
                                arrayParserState = ArrayParserState.FINISHED_OUTER_ARRAY;
                                return true;
                            default:
                                throw new RuntimeException("Unexpected token " + token);
                        }
                    } else {
                        return false;
                    }
                    break;
                case PARSE_VALUE:
                    if (itemParser.parseNext(jsonParser)) {
                        ParseResult<T> parseResult = itemParser.build();
                        if (parseResult == ParseResult.NULL_VALUE) {
                            items.add(null);
                        } else if (parseResult == ParseResult.END_ARRAY) {
                            arrayParserState = ArrayParserState.FINISHED_ARRAY;
                            return true;
                        } else {
                            T value = parseResult.getValue();
                            items.add(value);
                        }
                    } else {
                        return false;
                    }
                    break;
                default:
                    throw new RuntimeException("unexpected state " + arrayParserState);
            }
        }
    }

    @Override
    public ParseResult<List<T>> build() {
        switch (arrayParserState) {
            case FINISHED_ARRAY:
                arrayParserState = ArrayParserState.PARSE_START_ARRAY_OR_NULL_VALUE_OR_END_ARRAY;
                return new ParseResult.Value<>(Collections.unmodifiableList(items));
            case FINISHED_OUTER_ARRAY:
                arrayParserState = ArrayParserState.PARSE_START_ARRAY_OR_NULL_VALUE_OR_END_ARRAY;
                return ParseResult.endArray();
            case FINISHED_NULL:
                arrayParserState = ArrayParserState.PARSE_START_ARRAY_OR_NULL_VALUE_OR_END_ARRAY;
                return ParseResult.nullValue();
            default:
                throw new IllegalStateException("Parsing is not completed");
        }
    }

}
