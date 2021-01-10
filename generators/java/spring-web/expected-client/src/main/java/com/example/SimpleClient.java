package com.example;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

public class SimpleClient {
    private final RestOperations restOperations;
    private final String baseUrl;

    public SimpleClient(@Nonnull RestOperations restOperations, @Nonnull String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    @Nonnull
    public ResponseEntity<java.lang.String> simplePost(
            @Nonnull com.example.Dto dto
    ) {
        return simplePost$0(
                dto
        );
    }

    private ResponseEntity<java.lang.String> simplePost$0(
            com.example.Dto bodyArg
    ) {
        Map<String, Object> uriVariables = Collections.emptyMap();
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path1")

                .build(uriVariables);
        RequestEntity<com.example.Dto> request = RequestEntity
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bodyArg, com.example.Dto.class);
        return restOperations.exchange(request, java.lang.String.class);
    }

    @Nonnull
    public ResponseEntity<com.example.Dto> simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.Param2OfSimpleGet param2
    ) {
        return simpleGet$0(
                id,
            param1,
            param2
        );
    }

    private ResponseEntity<com.example.Dto> simpleGet$0(
            java.lang.String param0,
            java.lang.String param1,
            com.example.Param2OfSimpleGet param2
    ) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("id", param0 != null ? param0 : null);
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/path2/{id}")
                .queryParam("param1", param1 != null ? param1 : null)
                .queryParam("param2", param2 != null ? com.example.Param2OfSimpleGet.writeString(param2) : null)
                .build(uriVariables);
        RequestEntity<java.lang.Void> request = RequestEntity
                .get(uri)
                .build();
        return restOperations.exchange(request, com.example.Dto.class);
    }

}
