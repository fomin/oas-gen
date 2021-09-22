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

public class SimplePathsPath2IdGetProvider implements TestTemplateInvocationContextProvider {
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
        headers.put("Accept", new Parameter<String>(String.class, "application/json", java.util.function.Function.identity()));
        headers.put("param3", new Parameter<java.time.LocalDate>(java.time.LocalDate.class, null, p -> p.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)));
        pathParams.put("id", new Parameter<java.lang.String>(java.lang.String.class, null, p -> p));
        queryParams.put("param1", new Parameter<java.lang.String>(java.lang.String.class, null, p -> p));
        queryParams.put("param2", new Parameter<com.example.dto.Param2OfSimpleGet>(com.example.dto.Param2OfSimpleGet.class, null, p -> com.example.routes.Param2OfSimpleGetConverter.writeString(p)));
        Request request = new OriginRequest("simple get", "/path2/{id}", "GET", headers, null, pathParams, queryParams, null);
        return mutator.mutate(request).map(MutatedRequestInvocationContext::new);
    }
}
