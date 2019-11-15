package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import jsm.NonBlockingParser;
import jsm.ParseResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

public class SimpleMessageConverter implements HttpMessageConverter<Object> {

    private static final Set<Class<?>> SUPPORTED_CLASSES = new HashSet<>(Arrays.asList(
            com.example.Item.class,
            java.lang.String.class
    ));

    private final JsonFactory jsonFactory;

    public SimpleMessageConverter(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return mediaType != null
                && mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)
                && SUPPORTED_CLASSES.contains(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return mediaType != null
                && mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)
                && SUPPORTED_CLASSES.contains(clazz);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_JSON);
    }

    @Override
    public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
        NonBlockingJsonParser jsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
        InputStream inputStream = inputMessage.getBody();
        NonBlockingParser<?> parser;
        if (clazz == com.example.Item.class)
            parser = new com.example.Item.Parser();
        else if (clazz == java.lang.String.class)
            parser = jsm.ScalarParser.createStringParser();
        else
            throw new UnsupportedOperationException("Unsupported class " + clazz);
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) >= 0) {
            jsonParser.feedInput(buffer, 0, read);
            parser.parseNext(jsonParser);
        }
        ParseResult<?> parseResult = parser.build();
        if (parseResult == ParseResult.NULL_VALUE) {
            return null;
        } else {
            return parseResult.getValue();
        }
    }

    @Override
    public void write(Object obj, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        HttpHeaders headers = outputMessage.getHeaders();
        headers.add("Content-Type", "application/json");
        OutputStream outputStream = outputMessage.getBody();
        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {
            if (obj.getClass() == com.example.Item.class)
                com.example.Item.Writer.INSTANCE.write(jsonGenerator, (com.example.Item) obj);
            else if (obj.getClass() == java.lang.String.class)
                jsm.ScalarWriter.STRING_WRITER.write(jsonGenerator, (java.lang.String) obj);
            else
                throw new UnsupportedOperationException("Unsupported class " + obj.getClass());
        }
    }

}
