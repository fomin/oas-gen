package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import java.io.IOException;

public class SkipValueParser implements NonBlockingParser<Void> {
    private static final ParseResult.Value<Void> VOID_VALUE = new ParseResult.Value<>(null);
    private int depth = 0;

    @Override
    public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
        while (true) {
            JsonToken token;
            if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                switch (token) {
                    case START_OBJECT:
                    case START_ARRAY:
                        depth++;
                        break;
                    case END_OBJECT:
                    case END_ARRAY:
                        depth--;
                    default:
                        if (depth == 0) {
                            return true;
                        }
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public ParseResult<Void> build() {
        return VOID_VALUE;
    }
}
