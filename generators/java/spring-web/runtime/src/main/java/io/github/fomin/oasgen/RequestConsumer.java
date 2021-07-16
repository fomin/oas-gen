package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.util.List;

public class RequestConsumer {

    final String contentType;
    final IoConsumer<OutputStream> consumer;

    public RequestConsumer(String contentType, IoConsumer<OutputStream> consumer) {
        this.contentType = contentType;
        this.consumer = consumer;
    }

    public static <T> RequestConsumer json(T value, ValueWriter<T> requestValueWriter, ObjectMapper objectMapper) {
        return new RequestConsumer("application/json", outputStream -> {
            JsonGenerator jsonGenerator = objectMapper.createGenerator(outputStream);
            List<? extends ValidationError> errors = requestValueWriter.write(jsonGenerator, value);
            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }
            jsonGenerator.close();
        });
    }

}
