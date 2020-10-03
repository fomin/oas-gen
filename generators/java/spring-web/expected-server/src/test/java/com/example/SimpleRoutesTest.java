package com.example;

import io.github.fomin.oasgen.test.BaseServerTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleRoutesTest extends BaseServerTest {

    private static final int PORT = 8081;
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

    public SimpleRoutesTest() {
        super(PORT);
    }

    @SpringBootApplication
    @EnableWebMvc
    public static class TestApplication {

        @Bean
        public SimpleRoutes.Operations simpleRoutesActions() {
            return new SimpleRoutes.Operations() {
                @Override
                public ResponseEntity<String> create(@Nonnull Item item) {
                    assertNotNull(item);
                    return ResponseEntity.ok("id");
                }

                @Override
                public ResponseEntity<String> postWithoutRequestBody() {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                @Override
                public ResponseEntity<Item> find(@Nonnull String param1, @Nullable Param2OfFind param2) {
                    assertEquals("param1Value", param1);
                    assertEquals(Param2OfFind.VALUE2, param2);
                    return ResponseEntity.ok(TEST_ITEM);
                }

                @Override
                public ResponseEntity<Item> get(@Nonnull String id) {
                    assertEquals("idValue", id);
                    return ResponseEntity.ok(TEST_ITEM);
                }
            };
        }

        @Bean
        public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
            return factory -> {
                factory.setPort(PORT);
            };
        }
    }

    private static ConfigurableApplicationContext applicationContext;


    @BeforeAll
    public static void beforeAll() {
        applicationContext = SpringApplication.run(TestApplication.class);
    }

    @AfterAll
    public static void afterAll() {
        applicationContext.stop();
    }

}
