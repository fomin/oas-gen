package com.example;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface SimpleRoutes {

    @Nonnull
    @PostMapping(path = "/", produces = "application/json", consumes = "application/json")
    ResponseEntity<java.lang.String> create(
            @Nonnull @RequestBody com.example.Item item
    );

    @Nonnull
    @PostMapping(path = "/post-without-request-body", produces = "application/json")
    ResponseEntity<java.lang.String> postWithoutRequestBody(

    );

    @Nonnull
    @GetMapping(path = "/find", produces = "application/json")
    ResponseEntity<com.example.Item> find(
            @Nonnull @RequestParam("param1") java.lang.String param1,
            @Nullable @RequestParam("param2") java.lang.String param2
    );

    @Nonnull
    @GetMapping(path = "/{id}", produces = "application/json")
    ResponseEntity<com.example.Item> get(
            @Nonnull @PathVariable("id") java.lang.String id
    );

}
