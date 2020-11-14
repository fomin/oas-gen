package com.example;

import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.fomin.oasgen.test.BaseServerTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            Collections.singletonList(OffsetDateTime.of(2020, 11, 10, 1, 1, 1, 0, ZoneOffset.ofHours(1))),
            Collections.singletonMap("key 1", BigDecimal.TEN),
            Collections.singletonMap("key 1", OffsetDateTime.of(2020, 11, 10, 1, 1, 1, 0, ZoneOffset.ofHours(1))),
            new True("property 1 value"),
            new $1WithSpaceAndOtherÇhars("property 1 value")
    );

    public SimpleRoutesTest() {
        super(PORT);
    }

    @SpringBootApplication
    @EnableWebMvc
    public static class TestApplication implements WebMvcConfigurer {

        @Bean
        public SimpleRoutes.Operations simpleRoutesActions() {
            return new SimpleRoutes.Operations() {
                @Override
                public ResponseEntity<String> create(@Nonnull Item item) {
                    assertNotNull(item);
                    return ResponseEntity.ok("idValue");
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

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
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
