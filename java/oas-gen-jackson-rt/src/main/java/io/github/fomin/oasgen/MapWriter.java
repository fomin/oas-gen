package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Map;

public class MapWriter<T> implements Writer<Map<String, T>> {

    private final Writer<T> itemWriter;

    public MapWriter(Writer<T> itemWriter) {
        this.itemWriter = itemWriter;
    }

    @Override
    public void write(JsonGenerator jsonGenerator, Map<String, T> items) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<String, T> entry : items.entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey());
            itemWriter.write(jsonGenerator, entry.getValue());
        }
        jsonGenerator.writeEndObject();
    }
}
