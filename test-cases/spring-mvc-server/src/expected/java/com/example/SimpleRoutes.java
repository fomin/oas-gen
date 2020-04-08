package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface SimpleRoutes {

    @PostMapping(path = "/", produces = "application/json", consumes = "application/json")
    ResponseEntity<java.lang.String> create(
            @RequestBody com.example.Item item
    );

    @GetMapping(path = "/find", produces = "application/json")
    ResponseEntity<com.example.Item> find(
            @RequestParam("param1") java.lang.String param1,
            @RequestParam("param2") java.lang.String param2
    );

    @GetMapping(path = "/{id}", produces = "application/json")
    ResponseEntity<com.example.Item> get(
            @PathVariable("id") java.lang.String id
    );

}
