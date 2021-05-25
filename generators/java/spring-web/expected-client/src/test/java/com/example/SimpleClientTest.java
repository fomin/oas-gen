package com.example;

import com.example.dto.Dto;
import com.example.dto.Param2OfSimpleGet;
import com.example.routes.SimpleClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.EmptyRequestCallbacks;
import io.github.fomin.oasgen.SpringMvcClient;
import io.github.fomin.oasgen.test.ClientTest;
import io.github.fomin.oasgen.test.ReferenceServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import reactor.netty.DisposableServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleClientTest implements ClientTest {

    private static final int PORT = 8082;
    public static final Dto REFERENCE_DTO = new Dto("value1");

    @SpringBootApplication
    public static class TestApplication {

        @Bean
        public SimpleClient simpleClient() {
            SpringMvcClient springMvcClient = new SpringMvcClient(
                    new SimpleClientHttpRequestFactory(),
                    new DefaultResponseErrorHandler(),
                    new ObjectMapper(),
                    EmptyRequestCallbacks.INSTANCE
            );
            return new SimpleClient(springMvcClient, "http://localhost:" + PORT + ReferenceServer.BASE_PATH);
        }
    }

    private static DisposableServer server;
    private static ConfigurableApplicationContext applicationContext;
    private static SimpleClient simpleClient;

    @BeforeAll
    public static void beforeAll() {
        server = ReferenceServer.create(PORT);
        applicationContext = SpringApplication.run(TestApplication.class, String.valueOf(server.port()));
        simpleClient = applicationContext.getBean(SimpleClient.class);
    }

    @AfterAll
    public static void afterAll() {
        applicationContext.stop();
        server.disposeNow();
    }


    @Test
    @Override
    public void testPost() {
//        try {
//            Thread.sleep(10_000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        String response = simpleClient.simplePost(REFERENCE_DTO);
        assertEquals(ReferenceServer.POST_RESPONSE_VALUE_STR, response);
    }

    @Override
    @Test
    public void testGet() {
        Dto response = simpleClient.simpleGet("idValue", "param1Value", Param2OfSimpleGet.VALUE1);
        assertEquals(REFERENCE_DTO, response);
    }
}
