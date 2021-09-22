package com.example.routes;

import io.github.fomin.oasgen.DestructionTest;
import io.github.fomin.oasgen.Request;
import io.github.fomin.oasgen.RequestExecutor;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

public abstract class AbstractSimpleDestructionTest implements DestructionTest {

    public abstract RequestExecutor getExecutor();

    public abstract void healthCheck();

    public Request enrichRequest(Request request) {
        return request;
    }

    @TestTemplate
    @ExtendWith(com.example.routes.SimplePathsPath1PostProvider.class)
    public final void simplePostTest(Request request) {
        getExecutor().execute(enrichRequest(request));
        healthCheck();
    }

    @TestTemplate
    @ExtendWith(com.example.routes.SimplePathsPath2IdGetProvider.class)
    public final void simpleGetTest(Request request) {
        getExecutor().execute(enrichRequest(request));
        healthCheck();
    }

    @TestTemplate
    @ExtendWith(com.example.routes.SimplePathsPath3PostProvider.class)
    public final void testNullableParameterTest(Request request) {
        getExecutor().execute(enrichRequest(request));
        healthCheck();
    }

    @TestTemplate
    @ExtendWith(com.example.routes.SimplePathsPath4GetProvider.class)
    public final void returnOctetStreamTest(Request request) {
        getExecutor().execute(enrichRequest(request));
        healthCheck();
    }

    @TestTemplate
    @ExtendWith(com.example.routes.SimplePathsPath5PostProvider.class)
    public final void sendOctetStreamTest(Request request) {
        getExecutor().execute(enrichRequest(request));
        healthCheck();
    }
}
