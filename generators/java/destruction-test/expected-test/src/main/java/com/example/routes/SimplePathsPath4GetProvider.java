package com.example.routes;

import io.github.fomin.oasgen.MutatedRequestInvocationContext;
import io.github.fomin.oasgen.OriginRequest;
import io.github.fomin.oasgen.Parameter;
import io.github.fomin.oasgen.Request;
import io.github.fomin.oasgen.RequestMutator;
import io.github.fomin.oasgen.RequestMutatorImpl;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SimplePathsPath4GetProvider implements TestTemplateInvocationContextProvider {
    private final RequestMutator mutator = new RequestMutatorImpl();

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        Map<String, Parameter<?>> headers = new HashMap<>();
        Map<String, Parameter<?>> pathParams = new HashMap<>();
        Map<String, Parameter<?>> queryParams = new HashMap<>();
        headers.put("Accept", new Parameter<String>(String.class, "application/octet-stream", java.util.function.Function.identity()));
        Request request = new OriginRequest("return octet stream", "/path4", "GET", headers, null, pathParams, queryParams, null);
        return mutator.mutate(request).map(MutatedRequestInvocationContext::new);
    }
}
