package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public final class DtoTestUtils {

    private DtoTestUtils() {
    }

    public static final JsonFactory jsonFactory = new JsonFactory();

    public static <T> void assertSuccessfulParsing(
            T expectedValue,
            String input,
            NonBlockingParser<T> nonBlockingParser
    ) throws IOException {
        NonBlockingJsonParser jsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        jsonParser.feedInput(bytes, 0, bytes.length);
        jsonParser.endOfInput();
        assertTrue(nonBlockingParser.parseNext(jsonParser));
        ParseResult<T> parseResult = nonBlockingParser.build();
        assertEquals(expectedValue, parseResult.getValue());
    }

    public static <T> void assertSuccessfulWriting(
            String expectedOutput,
            T value,
            Writer<T> writer
    ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(baos);
        writer.write(jsonGenerator, value);
        jsonGenerator.close();
        byte[] expectedBytes = expectedOutput.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expectedBytes, baos.toByteArray());
    }
}
