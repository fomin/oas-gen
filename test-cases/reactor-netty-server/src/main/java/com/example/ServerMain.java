package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class ServerMain {
    public static void main(String[] args) throws InterruptedException {
        JsonFactory jsonFactory = new JsonFactory();
        DisposableServer httpServer = HttpServer.create()
                .port(8080)
                .route(new SimpleRoutes(jsonFactory, "") {
                    @Override
                    public Mono<String> create(Mono<Item> itemMono) {
                        return itemMono.map(it -> it.property1 + " " + it.property2.property21 + " " + it.property2.property22);
                    }

                    @Override
                    public Mono<Item> get(String id) {
                        return Mono.just(
                                new Item(
                                        "commonProperty1",
                                        "property1",
                                        new ItemProperty2(
                                                "commonProperty1",
                                                "property21",
                                                ItemProperty2Property22.VALUE1
                                        ),
                                        BigDecimal.ONE,
                                        LocalDateTime.now(),
                                        Arrays.asList("item1", "item2"),
                                        Collections.singletonMap("key", BigDecimal.ZERO)
                                )
                        );
                    }

                    @Override
                    public Mono<Item> find(String param1, String param2) {
                        return Mono.just(
                                new Item(
                                        "commonProperty1",
                                        "property1",
                                        new ItemProperty2(
                                                "commonProperty1",
                                                "property21",
                                                ItemProperty2Property22.VALUE1
                                        ),
                                        BigDecimal.ONE,
                                        LocalDateTime.now(),
                                        Arrays.asList("item1", "item2"),
                                        Collections.singletonMap("key", BigDecimal.ZERO)
                                )
                        );
                    }
                }).bindNow();

        Thread.sleep(1000000);
    }
}
