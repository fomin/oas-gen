package io.github.fomin.oasgen;

public interface DestructionTest {

    RequestExecutor getExecutor();

    void healthCheck();

    Request enrichRequest(Request request);
}
