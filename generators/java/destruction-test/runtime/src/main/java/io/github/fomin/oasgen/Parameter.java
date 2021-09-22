package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Parameter<T> {
    private final Class<T> clazz;
    private final T example;
    private final Function<T, String> writer;
    private final ValueWriter<T> bodyWriter;

    public Parameter(Class<T> clazz, T example, Function<T, String> writer) {

        this.clazz = clazz;
        this.example = example;
        this.writer = writer;
        this.bodyWriter = null;
    }

    public Parameter(Class<T> clazz, T example, ValueWriter<T> writer) {

        this.clazz = clazz;
        this.example = example;
        this.writer = null;
        this.bodyWriter = writer;
    }

    public Object getParam() {
        if (example != null) {
            return writer.apply(example);
        }
        return writer.apply(new PodamFactoryImpl().manufacturePojo(clazz));
    }

    public void writeParam(JsonGenerator generator) throws IOException {
        if (example != null) {
            bodyWriter.write(generator, example);
        }
        bodyWriter.write(generator, new PodamFactoryImpl().manufacturePojo(clazz));
    }
}
