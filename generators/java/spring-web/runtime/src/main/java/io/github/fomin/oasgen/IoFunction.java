package io.github.fomin.oasgen;

import java.io.IOException;

public interface IoFunction<T, R> {
    R accept(T t) throws IOException;
}
