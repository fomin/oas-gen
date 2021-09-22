package io.github.fomin.oasgen;

import java.util.stream.Stream;

public interface Mutator {
    boolean isAvailable(Request request);

    Stream<Request> mutate(Request request);
}
