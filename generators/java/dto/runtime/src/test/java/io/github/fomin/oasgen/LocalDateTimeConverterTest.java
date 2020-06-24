package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeConverterTest {

    private static <T> void parserTest(T expected, NonBlockingParser<T> parser, String json) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        NonBlockingJsonParser jsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
        jsonParser.feedInput(json.getBytes(), 0, json.length());
        boolean done = parser.parseNext(jsonParser);
        assertTrue(done);
        ParseResult<T> build = parser.build();
        assertEquals(expected, build.getValue());
    }

    @Test
    public void basicParsingTest() throws IOException {
        parserTest(
                LocalDateTime.of(2020, 1, 1, 1, 1, 1),
                LocalDateTimeConverter.createParser(),
                "\"2020-01-01T01:01:01\""
        );
    }

    @Test
    public void parsingWithPattern() throws IOException {
        parserTest(
                LocalDateTime.of(2020, 1, 1, 1, 1, 1),
                LocalDateTimeConverter.createParser("dd.MM.yyyy HH:mm:ss"),
                "\"01.01.2020 01:01:01\""
        );
    }

}
