package com.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleClientTest implements ClientTest {

    private static final int PORT = 8082;
    public static final Dto REFERENCE_DTO = new Dto("value1");

    @SpringBootApplication
    public static class TestApplication {

        @Bean
        public SimpleClient simpleClient() {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            builder.serializers(new StdScalarSerializer<BigDecimal>(BigDecimal.class) {
                @Override
                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                    gen.writeString(value.toPlainString());
                }
            });
            builder.deserializers(new StdScalarDeserializer<BigDecimal>(BigDecimal.class) {
                @Override
                public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return new BigDecimal(p.getText());
                }
            });
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


    @Test
    @Override
    public void testPost() {
        ResponseEntity<String> responseEntity = simpleClient.simplePost(REFERENCE_DTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ReferenceServer.POST_RESPONSE_VALUE_STR, responseEntity.getBody());
    }

    @Override
    @Test
    public void testGet() {
        ResponseEntity<Dto> responseEntity = simpleClient.simpleGet("idValue", "param1Value", Param2OfSimpleGet.VALUE1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(REFERENCE_DTO, responseEntity.getBody());
    }
}
