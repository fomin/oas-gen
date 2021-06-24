package com.example.builtin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltinTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    interface BiConsumerWithException<A, B> {
        void accept(A a, B b) throws IOException;
    }

    @Test
    public void testDecimal() throws IOException {
        checkTestCase(
                "\"10.0\"",
                new BigDecimal("10.0"),
                Builtin::parseDecimal,
                Builtin::writeDecimal
        );
    }

    @Test
    public void testInt32() throws IOException {
        checkTestCase(
                "10",
                10,
                Builtin::parseInt32,
                Builtin::writeInt32
        );
    }

    private static <T> void checkTestCase(
            String json,
            T value,
            Function<JsonNode, T> parseFunction,
            BiConsumerWithException<JsonGenerator, T> writeConsumer
    ) throws IOException {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
        assertEquals(value, parseFunction.apply(jsonNode));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator generator = OBJECT_MAPPER.createGenerator(baos);
        writeConsumer.accept(generator, value);
        generator.close();
        assertEquals(json, baos.toString("UTF-8"));
    }

}
