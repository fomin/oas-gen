package io.github.fomin.oasgen.mutator;

import io.github.fomin.oasgen.MutatedRequest;
import io.github.fomin.oasgen.Mutator;
import io.github.fomin.oasgen.MutatorUtils;
import io.github.fomin.oasgen.Request;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QueryParamsMutator implements Mutator {
    @Override
    public boolean isAvailable(Request request) {
        return true;
    }

    @Override
    public Stream<Request> mutate(Request request) {
        List<Request> result = new ArrayList<>();
        MutatedRequest mr = new MutatedRequest(request, this);
        mr.addQueryParam(RandomStringUtils.randomAlphanumeric(5), RandomStringUtils.randomAlphanumeric(10));
        result.add(mr);

        if (!request.getQueryParams().isEmpty()) {
            mr = new MutatedRequest(request, this);
            mr.removeQueryParam(MutatorUtils.random(request.getQueryParams().keySet()));
            result.add(mr);
        }

        return result.stream();
    }

    @Override
    public String toString() {
        return "QueryParamsMutator";
    }
}
