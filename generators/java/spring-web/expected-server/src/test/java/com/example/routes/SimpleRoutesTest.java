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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
                        @Nullable Param2OfSimpleGet param2,
                        @Nullable java.time.LocalDate param3
                ) {
                    assertEquals("idValue", id);
                    assertEquals("param1Value", param1);
                    assertEquals(Param2OfSimpleGet.VALUE1, param2);
                    assertEquals(LocalDate.parse("2021-06-01"), param3);
                    return REFERENCE_DTO;
                }

                @Override
                public void testNullableParameter(@Nullable LocalDate param1) {
                    assertNull(param1);
                }

                @Override
                public void returnOctetStream(@Nonnull OutputStream outputStream) throws java.io.IOException {
                    outputStream.write(TEST_BYTES);
                    outputStream.flush();
                }

                @Override
                public void sendOctetStream(@Nonnull InputStream inputStream) throws IOException {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1000];
                    int read = inputStream.read(buffer);
                    while (read > 0) {
                        byteArrayOutputStream.write(buffer, 0, read);
                        read = inputStream.read(buffer);
                    }
                    assertArrayEquals(TEST_BYTES, byteArrayOutputStream.toByteArray());
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
