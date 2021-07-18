package io.github.fomin.oasgen;

import java.io.IOException;

public interface IoConsumer<T> {
    void accept(T t) throws IOException;
}
