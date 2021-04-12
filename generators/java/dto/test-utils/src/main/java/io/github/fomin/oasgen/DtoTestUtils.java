package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public final class DtoTestUtils {

    public interface WriteFunction<T> {
        List<? extends ValidationError> apply(JsonGenerator jsonGenerator, T t) throws IOException;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DtoTestUtils() {
    }

    public static final JsonFactory jsonFactory = new JsonFactory();

    public static <T> void assertSuccessfulParsing(
            T expectedValue,
            String input,
            Function<JsonNode, T> parseFunction
    ) throws IOException {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        JsonNode jsonNode = objectMapper.readTree(bytes);
        T value = parseFunction.apply(jsonNode);
        assertEquals(expectedValue, value);
    }

    public static <T> void assertSuccessfulWriting(
            String expectedOutput,
            T value,
            WriteFunction<T> writeFunction
    ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(baos);
        List<? extends ValidationError> errors = writeFunction.apply(jsonGenerator, value);
        assertTrue(errors.isEmpty());
        jsonGenerator.close();
        byte[] expectedBytes = expectedOutput.getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expectedBytes, baos.toByteArray());
    }

    public static void assertParsingValidationErrors(
            List<? extends ValidationError> expectedErrors,
            String input,
            Function<JsonNode, ?> parseFunction
    ) throws IOException {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        JsonNode jsonNode = objectMapper.readTree(bytes);
        try {
            parseFunction.apply(jsonNode);
            Assertions.fail("Validation exception is expected");
        } catch (ValidationException e) {
            Assertions.assertEquals(expectedErrors, e.validationErrors);
        }
    }
}
