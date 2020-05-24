package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import io.github.fomin.oasgen.test.ClientTest;
import io.github.fomin.oasgen.test.ReferenceServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleClientTest implements ClientTest {

    private static final int PORT = 8083;

    private static DisposableServer referenceServer;
    private static SimpleClient simpleClient;

    @BeforeAll
    public static void beforeAll() {
        referenceServer = ReferenceServer.create(PORT);
        HttpClient httpClient = HttpClient.create().baseUrl("http://localhost:" + PORT + ReferenceServer.BASE_PATH);
        simpleClient = new SimpleClient(new JsonFactory(), httpClient);
    }

    @AfterAll
    public static void afterAll() {
        referenceServer.disposeNow();
    }

    @Override
    public void testFind() {
        Mono<Item> itemMono = simpleClient.find("param1Value", "param2Value");
        Item item = itemMono.block();
        assertNotNull(item);
    }

    @Test
    @Override
    public void testGet() {
        Mono<Item> itemMono = simpleClient.get("idValue");
        Item item = itemMono.block();
        assertNotNull(item);
    }

    @Test
    @Override
    public void testCreate() {
        Mono<String> idMono = simpleClient.create(
                Mono.just(
                        new Item(
                                "common property 1 value",
                                "property 1 value",
                                new ItemProperty2(
                                        "inner common property 1 value",
                                        "property 21 value",
                                        ItemProperty2Property22.VALUE1
                                ),
                                BigDecimal.ONE,
                                LocalDateTime.of(2020, 1, 1, 1, 1),
                                Arrays.asList("array value 1", "array value 2"),
                                Collections.singletonMap("key 1", BigDecimal.TEN)
                        )
                )
        );
        String id = idMono.block();
        assertEquals("idValue", id);
    }
}
