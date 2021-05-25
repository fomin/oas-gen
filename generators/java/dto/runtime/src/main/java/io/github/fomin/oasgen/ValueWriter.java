package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.List;

public interface ValueWriter<T> {
    List<? extends ValidationError> write(JsonGenerator jsonGenerator, T t) throws IOException;
}
