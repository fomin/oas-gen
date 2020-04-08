package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class ClientMain {
    public static void main(String[] args) {

        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> tcpClient.host("localhost"))
                .port(8080);
        SimpleClient simpleClient = new SimpleClient(new JsonFactory(), httpClient);
        Mono<String> idMono = simpleClient.create(
                Mono.just(
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
                )
        );
        String id = idMono.block();
        System.out.println(id);

        Mono<Item> itemMono = simpleClient.get("id");
        Item item = itemMono.block();
        System.out.println(item);

        Mono<Item> foundItemMono = simpleClient.find("value 1", "value 2");
        Item foundItem = foundItemMono.block();
        System.out.println(foundItem);
    }
}
