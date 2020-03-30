package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface Writer<T> {
    void write(JsonGenerator jsonGenerator, T t) throws IOException;
}
