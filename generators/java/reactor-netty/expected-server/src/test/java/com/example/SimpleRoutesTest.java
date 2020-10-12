package com.example;

import io.github.fomin.oasgen.test.BaseServerTest;
import io.github.fomin.oasgen.test.ReferenceServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import reactor.netty.DisposableServer;

public class SimpleRoutesTest extends BaseServerTest {
    private static final int PORT = 8084;

    private static DisposableServer disposableServer;

    public SimpleRoutesTest() {
        super(PORT);
    }

    @BeforeAll
    public static void beforeAll() {
        disposableServer = ReferenceServer.create(PORT);
    }

    @AfterAll
    public static void afterAll() {
        disposableServer.disposeNow();
    }

}
