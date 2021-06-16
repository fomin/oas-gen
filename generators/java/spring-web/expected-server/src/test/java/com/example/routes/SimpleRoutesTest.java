package com.example.routes;

import com.example.dto.Dto;
import com.example.dto.Param2OfSimpleGet;
import io.github.fomin.oasgen.test.BaseServerTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleRoutesTest extends BaseServerTest {

    private static final int PORT = 9081;
    public static final Dto REFERENCE_DTO = new Dto("value1");

    public SimpleRoutesTest() {
        super(PORT);
    }

    @SpringBootApplication
    @EnableWebMvc
    public static class TestApplication implements WebMvcConfigurer {

        @Bean
        public SimpleOperations simpleRoutesActions() {
            return new SimpleOperations() {
                @Override
                public String simplePost(@Nonnull Dto dto) {
                    assertEquals(REFERENCE_DTO, dto);
                    return "postResponseValue";
                }

                @Override
                public Dto simpleGet(
                        @Nonnull String id,
                        @Nonnull String param1,
                        @Nullable Param2OfSimpleGet param2
                ) {
                    assertEquals("idValue", id);
                    assertEquals("param1Value", param1);
                    assertEquals(Param2OfSimpleGet.VALUE1, param2);
                    return REFERENCE_DTO;
                }
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
