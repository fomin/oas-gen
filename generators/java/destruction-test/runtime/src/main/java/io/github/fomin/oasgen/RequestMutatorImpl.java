package io.github.fomin.oasgen;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class RequestMutatorImpl implements RequestMutator {
    private final List <Mutator> mutators = new ArrayList<>();

    public RequestMutatorImpl() {
        for (Mutator mutator : ServiceLoader.load(Mutator.class)) {
            mutators.add(mutator);
        }
    }

    @Override
    public Stream<Request> mutate(Request origin) {
        return mutators.stream()
                .filter(m -> m.isAvailable(origin))
                .flatMap(m -> m.mutate(origin));
    }
}
