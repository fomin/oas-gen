package io.github.fomin.oasgen;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.Collections;
import java.util.List;

public class MutatedRequestInvocationContext implements TestTemplateInvocationContext {
    private final Request request;

    public MutatedRequestInvocationContext(Request request) {
        this.request = request;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return request.toString();
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new RequestResolver(request));
    }
}
