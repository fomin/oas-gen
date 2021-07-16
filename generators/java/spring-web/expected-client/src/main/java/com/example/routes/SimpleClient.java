package com.example.routes;

import io.github.fomin.oasgen.IoConsumer;
import io.github.fomin.oasgen.RequestConsumer;
import io.github.fomin.oasgen.ResponseFunction;
import io.github.fomin.oasgen.SpringMvcClient;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

public class SimpleClient {
    private final SpringMvcClient springMvcClient;
    private final String baseUrl;

    public SimpleClient(@Nonnull SpringMvcClient springMvcClient, @Nonnull String baseUrl) {
        this.springMvcClient = springMvcClient;
        this.baseUrl = baseUrl;
    }

    @Nonnull
    public java.lang.String simplePost(
            @Nonnull com.example.dto.Dto dto
    ) {
        return simplePost$0(
                dto
        );
    }

    private java.lang.String simplePost$0(
            com.example.dto.Dto bodyArg
    ) {
        Map<String, Object> uriVariables = Collections.emptyMap();
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path1")

                .build(uriVariables);
        return springMvcClient.doRequest(
                uri,
                HttpMethod.POST,
                headers -> {

                },
                RequestConsumer.json(
                        bodyArg,
                        (jsonGenerator, value) -> com.example.routes.DtoConverter.write(jsonGenerator, value),
                        springMvcClient.objectMapper
                ),
                ResponseFunction.json(
                        jsonNode -> io.github.fomin.oasgen.StringConverter.parse(jsonNode),
                        springMvcClient.objectMapper
                )
        );
    }

    @Nonnull
    public com.example.dto.Dto simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.dto.Param2OfSimpleGet param2,
            @Nullable java.time.LocalDate param3
    ) {
        return simpleGet$0(
                id,
                param1,
                param2,
                param3
        );
    }

    private com.example.dto.Dto simpleGet$0(
            java.lang.String param0,
            java.lang.String param1,
            com.example.dto.Param2OfSimpleGet param2,
            java.time.LocalDate param3
    ) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("id", param0 != null ? param0 : null);
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path2/{id}")
                .queryParam("param1", param1 != null ? param1 : null)
                .queryParam("param2", param2 != null ? com.example.routes.Param2OfSimpleGetConverter.writeString(param2) : null)
                .build(uriVariables);
        return springMvcClient.doRequest(
                uri,
                HttpMethod.GET,
                headers -> {
                    if (param3 != null) {
                        headers.set("param3", param3.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                },
                null,
                ResponseFunction.json(
                        jsonNode -> com.example.routes.DtoConverter.parse(jsonNode),
                        springMvcClient.objectMapper
                )
        );
    }

    @Nonnull
    public java.lang.Void testNullableParameter(
            @Nullable java.time.LocalDate param1
    ) {
        return testNullableParameter$0(
                param1
        );
    }

    private java.lang.Void testNullableParameter$0(
            java.time.LocalDate param0
    ) {
        Map<String, Object> uriVariables = Collections.emptyMap();
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path3")
                .queryParam("param1", param0 != null ? param0.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .build(uriVariables);
        return springMvcClient.doRequest(
                uri,
                HttpMethod.POST,
                headers -> {

                },
                null,
                null
        );
    }

    @Nonnull
    public java.lang.Void returnOctetStream(
            IoConsumer<java.io.InputStream> inputStreamConsumer
    ) {
        return returnOctetStream$0(
                inputStreamConsumer
        );
    }

    private java.lang.Void returnOctetStream$0(
            IoConsumer<java.io.InputStream> inputStreamConsumer
    ) {
        Map<String, Object> uriVariables = Collections.emptyMap();
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path4")

                .build(uriVariables);
        return springMvcClient.doRequest(
                uri,
                HttpMethod.GET,
                headers -> {

                },
                null,
                new ResponseFunction<>("application/octet-stream", inputStream -> {
                    inputStreamConsumer.accept(inputStream);
                    return null;
                })
        );
    }

    @Nonnull
    public java.lang.Void sendOctetStream(
            IoConsumer<java.io.OutputStream> outputStreamIoConsumer
    ) {
        return sendOctetStream$0(
                outputStreamIoConsumer
        );
    }

    private java.lang.Void sendOctetStream$0(
            IoConsumer<java.io.OutputStream> outputStreamIoConsumer
    ) {
        Map<String, Object> uriVariables = Collections.emptyMap();
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path5")

                .build(uriVariables);
        return springMvcClient.doRequest(
                uri,
                HttpMethod.POST,
                headers -> {

                },
                new RequestConsumer("application/octet-stream", outputStreamIoConsumer),
                null
        );
    }

}
