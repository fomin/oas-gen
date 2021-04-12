package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.DefaultHandlerAdapterMapping;
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

    private static final int PORT = 8081;
    public static final Dto REFERENCE_DTO = new Dto("value1");

    public SimpleRoutesTest() {
        super(PORT);
    }

    @SpringBootApplication
    @EnableWebMvc
    public static class TestApplication implements WebMvcConfigurer {

        @Bean
        public DefaultHandlerAdapterMapping defaultHandlerMapping() {
            return new DefaultHandlerAdapterMapping(simpleHandlerAdapter(), BaseServerTest.CONTEXT_PATH);
        }

        @Bean
        public SimpleHandlerAdapter simpleHandlerAdapter() {
            return new SimpleHandlerAdapter(BaseServerTest.CONTEXT_PATH, simpleRoutesActions(), new ObjectMapper());
        }

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
//
//        @Override
//        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//            builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
//            builder.serializers(new StdScalarSerializer<BigDecimal>(BigDecimal.class) {
//                @Override
//                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
//                    gen.writeString(value.toPlainString());
//                }
//            });
//            builder.deserializers(new StdScalarDeserializer<BigDecimal>(BigDecimal.class) {
//                @Override
//                public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//                    return new BigDecimal(p.getText());
//                }
//            });
//            converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        }
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
