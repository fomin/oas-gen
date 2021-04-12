package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.List;

// TODO REMOVE
public class ArrayWriter<T> implements Writer<List<T>> {

    private final Writer<T> itemWriter;

    public ArrayWriter(Writer<T> itemWriter) {
        this.itemWriter = itemWriter;
    }

    @Override
    public void write(JsonGenerator jsonGenerator, List<T> items) throws IOException {
        jsonGenerator.writeStartArray();
        for (T item : items) {
            itemWriter.write(jsonGenerator, item);
        }
        jsonGenerator.writeEndArray();
    }
}
