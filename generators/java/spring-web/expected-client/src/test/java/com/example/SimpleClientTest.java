package com.example;

import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import reactor.netty.DisposableServer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            Collections.singletonList(OffsetDateTime.of(2020, 11, 10, 1, 1, 1, 0, ZoneOffset.ofHours(1))),
            Collections.singletonMap("key 1", BigDecimal.TEN),
            Collections.singletonMap("key 1", OffsetDateTime.of(2020, 11, 10, 1, 1, 1, 0, ZoneOffset.ofHours(1))),
            new True("property 1 value"),
            new $1WithSpaceAndOtherÇhars("property 1 value")
    );

    @SpringBootApplication
    public static class TestApplication {

        @Bean
        public SimpleClient simpleClient() {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            List<HttpMessageConverter<?>> converters = Collections.singletonList(
                    new MappingJackson2HttpMessageConverter(builder.build())
            );
            RestOperations restOperations = new RestTemplate(converters);
            return new SimpleClient(restOperations, "http://localhost:" + PORT + ReferenceServer.BASE_PATH);
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
        ResponseEntity<Item> responseEntity = simpleClient.find("param1Value", Param2OfFind.VALUE2);
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
        assertEquals("idValue", responseEntity.getBody());
    }
}
