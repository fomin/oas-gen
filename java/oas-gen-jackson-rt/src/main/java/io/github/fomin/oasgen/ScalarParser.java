package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class ScalarParser<T> implements NonBlockingParser<T> {

    public interface Reader<T> {
        T readValue(JsonParser jsonParser) throws IOException;
    }

    public static ScalarParser<String> createStringParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                JsonParser::getText
        );
    }

    public static ScalarParser<LocalDateTime> createStringLocalDateTimeParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> LocalDateTime.parse(jsonParser.getText())
        );
    }

    public static ScalarParser<BigDecimal> createNumberParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT,
                JsonParser::getDecimalValue
        );
    }

    private final Predicate<JsonToken> jsonTokenPredicate;
    private final Reader<T> reader;
    private ScalarParserState scalarParserState = ScalarParserState.PARSE_VALUE_OR_NULL_VALUE_OR_END_ARRAY;
    private T value;

    public ScalarParser(Predicate<JsonToken> jsonTokenPredicate, Reader<T> reader) {
        this.jsonTokenPredicate = jsonTokenPredicate;
        this.reader = reader;
    }

    @Override
    public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
        if (jsonParser.currentToken() == null || jsonParser.currentToken() != JsonToken.NOT_AVAILABLE) {
            JsonToken token;
            if (scalarParserState == ScalarParserState.PARSE_VALUE_OR_NULL_VALUE_OR_END_ARRAY) {
                if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                    if (jsonTokenPredicate.test(token)) {
                        scalarParserState = ScalarParserState.FINISHED_VALUE;
                        value = reader.readValue(jsonParser);
                        return true;
                    } else if (token == JsonToken.VALUE_NULL) {
                        scalarParserState = ScalarParserState.FINISHED_NULL;
                        value = null;
                        return true;
                    } else if (token == JsonToken.END_ARRAY) {
                        scalarParserState = ScalarParserState.FINISHED_ARRAY;
                        value = null;
                        return true;
                    }
                    throw new RuntimeException("Unexpected token " + token + " " + jsonParser.getValueAsString());
                }
            } else {
                throw new RuntimeException("unexpected state " + scalarParserState);
            }
        }
        return false;
    }

    @Override
    public ParseResult<T> build() {
        switch (scalarParserState) {
            case FINISHED_NULL:
                scalarParserState = ScalarParserState.PARSE_VALUE_OR_NULL_VALUE_OR_END_ARRAY;
                return ParseResult.nullValue();
            case FINISHED_ARRAY:
                scalarParserState = ScalarParserState.PARSE_VALUE_OR_NULL_VALUE_OR_END_ARRAY;
                return ParseResult.endArray();
            case FINISHED_VALUE:
                scalarParserState = ScalarParserState.PARSE_VALUE_OR_NULL_VALUE_OR_END_ARRAY;
                return new ParseResult.Value<>(value);
            default:
                throw new IllegalStateException("Parsing is not completed");
        }
    }

}
