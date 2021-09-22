package io.github.fomin.oasgen.mutator;

import io.github.fomin.oasgen.MutatedRequest;
import io.github.fomin.oasgen.Mutator;
import io.github.fomin.oasgen.MutatorUtils;
import io.github.fomin.oasgen.Request;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.Stream;

public class PathParamsMutator implements Mutator {
    @Override
    public boolean isAvailable(Request request) {
        return !request.getPathParams().isEmpty();
    }

    @Override
    public Stream<Request> mutate(Request request) {
        MutatedRequest mr = new MutatedRequest(request, this);
        mr.updatePathParam(MutatorUtils.random(request.getPathParams().keySet()), RandomStringUtils.randomAlphanumeric(10));
        return Stream.of(mr);
    }

    @Override
    public String toString() {
        return "PathParamsMutator";
    }
}
