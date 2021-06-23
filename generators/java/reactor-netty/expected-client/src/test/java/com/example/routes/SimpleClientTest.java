package com.example.routes;

import com.example.dto.Dto;
import com.example.dto.Param2OfSimpleGet;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.test.ClientTest;
import io.github.fomin.oasgen.test.ReferenceServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleClientTest implements ClientTest {

    private static final int PORT = 9083;
    public static final Dto REFERENCE_DTO = new Dto("value1");

    private static DisposableServer referenceServer;
    private static SimpleClient simpleClient;

    @BeforeAll
    public static void beforeAll() {
        referenceServer = ReferenceServer.create(PORT);
        HttpClient httpClient = HttpClient.create().baseUrl("http://localhost:" + PORT + ReferenceServer.BASE_PATH);
        simpleClient = new SimpleClient(new ObjectMapper(), httpClient);
    }

    @AfterAll
    public static void afterAll() {
        referenceServer.disposeNow();
    }

    @Test
    @Override
    public void testPost() {
        Mono<String> response = simpleClient.simplePost(Mono.just(REFERENCE_DTO));
        assertEquals(ReferenceServer.POST_RESPONSE_VALUE_STR, response.block());
    }

    @Test
    @Override
    public void testGet() {
        Mono<Dto> response = simpleClient.simpleGet("idValue", "param1Value", Param2OfSimpleGet.VALUE1, LocalDate.parse("2021-06-01"));
        assertEquals(REFERENCE_DTO, response.block());
    }

    @Test
    @Override
    public void testNullableParameter() {
        Mono<Void> response = simpleClient.testNullableParameter(null);
        response.block();
    }
}
