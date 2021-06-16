package com.example.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.DefaultHandlerAdapterMapping;
import io.github.fomin.oasgen.MatchingHandlerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;

@Configuration
public class SimpleConfiguration {

    private final String basePath;
    private final SimpleOperations simpleOperations;
    private final ObjectMapper objectMapper;

    public SimpleConfiguration(
            @Value("${com.example.routes.SimpleOperations.basePath:}") String basePath,
            SimpleOperations simpleOperations,
            ObjectMapper objectMapper
    ) {
        this.basePath = basePath;
        this.simpleOperations = simpleOperations;
        this.objectMapper = objectMapper;
    }

    @Bean
    public HandlerMapping simpleHandlerMapping() {
        return new DefaultHandlerAdapterMapping(simpleHandlerAdapter());
    }

    @Bean
    public MatchingHandlerAdapter simpleHandlerAdapter() {
        return new SimpleHandlerAdapter(basePath, simpleOperations, objectMapper);
    }
}
