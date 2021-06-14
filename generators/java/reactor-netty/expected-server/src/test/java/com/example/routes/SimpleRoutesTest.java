package com.example.routes;

import com.example.dto.Dto;
import com.example.dto.Param2OfSimpleGet;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.test.BaseServerTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleRoutesTest extends BaseServerTest {
    private static final int PORT = 9084;

    private static DisposableServer disposableServer;

    public SimpleRoutesTest() {
        super(PORT);
    }

    @BeforeAll
    public static void beforeAll() {
        SimpleRoutes simpleRoutes = new SimpleRoutes(new ObjectMapper(), "/base") {
            @Nonnull
            @Override
            public Mono<String> simplePost(@Nonnull Mono<Dto> requestBodyMono) {
                return Mono.just("postResponseValue");
            }

            @Nonnull
            @Override
            public Mono<Dto> simpleGet(@Nonnull String id, @Nonnull String param1, @Nullable Param2OfSimpleGet param2) {
                return Mono.just(new Dto("value1"));
            }
        };
        disposableServer = HttpServer.create().host("127.0.0.1").port(PORT).route(simpleRoutes).bindNow();
    }

    @AfterAll
    public static void afterAll() {
        disposableServer.disposeNow();
    }

}
