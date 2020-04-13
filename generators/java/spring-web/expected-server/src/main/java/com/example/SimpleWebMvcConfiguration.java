package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class SimpleWebMvcConfiguration extends WebMvcConfigurationSupport {

    private final JsonFactory jsonFactory;

    public SimpleWebMvcConfiguration(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new SimpleMessageConverter(jsonFactory));
    }

}
