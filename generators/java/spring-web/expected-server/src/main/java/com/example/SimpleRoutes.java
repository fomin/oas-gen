package com.example;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("${com.example.SimpleRoutes.path}")
public class SimpleRoutes {
    public interface Operations {
        ResponseEntity<java.lang.String> create(
                @Nonnull com.example.Item item
        );

        ResponseEntity<java.lang.String> postWithoutRequestBody(

        );

        ResponseEntity<com.example.Item> find(
                @Nonnull java.lang.String param1,
                @Nullable com.example.Param2OfFind param2
        );

        ResponseEntity<com.example.Item> get(
                @Nonnull java.lang.String id
        );
    }

    public final Operations operations;

    public SimpleRoutes(Operations operations) {
        this.operations = operations;
    }

    @PostMapping(path = "/", produces = "application/json", consumes = "application/json")
    public ResponseEntity<java.lang.String> create(
            @Nonnull @RequestBody com.example.Item item
    ) {
        return this.operations.create(
                item
        );
    }

    @PostMapping(path = "/post-without-request-body", produces = "application/json")
    public ResponseEntity<java.lang.String> postWithoutRequestBody(

    ) {
        return this.operations.postWithoutRequestBody(

        );
    }

    @GetMapping(path = "/find", produces = "application/json")
    public ResponseEntity<com.example.Item> find(
            @Nonnull @RequestParam("param1") java.lang.String param1,
            @Nullable @RequestParam("param2") java.lang.String param2
    ) {
        return this.operations.find(
                param1 != null ? param1 : null,
                param2 != null ? com.example.Param2OfFind.parseString(param2) : null
        );
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<com.example.Item> get(
            @Nonnull @PathVariable("id") java.lang.String id
    ) {
        return this.operations.get(
                id != null ? id : null
        );
    }

}
