package com.example;

import io.github.fomin.oasgen.test.ClientTest;
import io.github.fomin.oasgen.test.ReferenceServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.netty.DisposableServer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleClientTest implements ClientTest {

    private static final int PORT = 8082;
    private static final Item TEST_ITEM = new Item(
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
    );

    @SpringBootApplication
    public static class TestApplication {

        @Bean
        public SimpleClient simpleClient() {
            return new SimpleClient(new RestTemplate(), "http://localhost:" + PORT + ReferenceServer.BASE_PATH);
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

    @Override
    @Test
    public void testFind() {
        ResponseEntity<Item> responseEntity = simpleClient.find("param1Value", "param2Value", EnumItem.VALUE_1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Override
    @Test
    public void testGet() {
        ResponseEntity<Item> responseEntity = simpleClient.get("idValue");
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Override
    @Test
    public void testCreate() {
        ResponseEntity<String> responseEntity = simpleClient.create(TEST_ITEM);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("\"idValue\"", responseEntity.getBody());
    }
}
