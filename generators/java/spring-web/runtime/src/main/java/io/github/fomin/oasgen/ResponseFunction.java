package io.github.fomin.oasgen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.function.Function;

public class ResponseFunction<R> {
    final String contentType;
    final IoFunction<InputStream, R> function;

    public ResponseFunction(String contentType, IoFunction<InputStream, R> function) {
        this.contentType = contentType;
        this.function = function;
    }

    public static <R> ResponseFunction<R> json(Function<JsonNode, R> parseFunction, ObjectMapper objectMapper) {
        return new ResponseFunction<>("application/json", inputStream -> {
            JsonNode responseJsonNode = objectMapper.readTree(inputStream);
            return parseFunction.apply(responseJsonNode);
        });
    }
}
