package io.github.fomin.oasgen;

import java.util.stream.Stream;

public interface RequestMutator {
    Stream<Request> mutate(Request origin);
}
