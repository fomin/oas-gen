package com.example;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("${com.example.SimpleRoutes.path:}")
public class SimpleRoutes {
    public interface Operations {
        ResponseEntity<java.lang.String> simplePost(
                @Nonnull com.example.Dto dto
        );

        ResponseEntity<com.example.Dto> simpleGet(
                @Nonnull java.lang.String id,
                @Nonnull java.lang.String param1,
                @Nullable com.example.Param2OfSimpleGet param2
        );
    }

    public final Operations operations;

    public SimpleRoutes(Operations operations) {
        this.operations = operations;
    }

    @PostMapping(path = "/path1", produces = "application/json", consumes = "application/json")
    public ResponseEntity<java.lang.String> simplePost(
            @Nonnull @RequestBody com.example.Dto dto
    ) {
        return this.operations.simplePost(
                dto
        );
    }

    @GetMapping(path = "/path2/{id}", produces = "application/json")
    public ResponseEntity<com.example.Dto> simpleGet(
            @Nonnull @PathVariable("id") java.lang.String id,
            @Nonnull @RequestParam("param1") java.lang.String param1,
            @Nullable @RequestParam("param2") java.lang.String param2
    ) {
        return this.operations.simpleGet(
                id != null ? id : null,
                param1 != null ? param1 : null,
                param2 != null ? com.example.Param2OfSimpleGet.parseString(param2) : null
        );
    }

}
