package com.example;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
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

        ResponseEntity<org.springframework.core.io.Resource> downloadfile(
                @Nonnull org.springframework.core.io.Resource file,
                @Nonnull java.lang.String name
        );

        ResponseEntity<org.springframework.core.io.Resource> downloadWithModel(
                @Nonnull org.springframework.core.io.Resource file,
                @Nonnull com.example.DownloadWithModelRequestParams params
        );

        ResponseEntity<MultiValueMap<String, Object>> downloadWithType(
                @Nonnull org.springframework.core.io.Resource file
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

    @PostMapping(path = "/download", produces = "multipart/form-data", consumes = "multipart/form-data")
    public ResponseEntity<org.springframework.core.io.Resource> downloadfile(
            @Nonnull @RequestPart("file") org.springframework.core.io.Resource file,
            @Nonnull @RequestPart("name") java.lang.String name
    ) {
        return this.operations.downloadfile(
                file,
                name
        );
    }

    @PostMapping(path = "/downloadWithModel", produces = "multipart/form-data", consumes = "multipart/form-data")
    public ResponseEntity<org.springframework.core.io.Resource> downloadWithModel(
            @Nonnull @RequestPart("file") org.springframework.core.io.Resource file,
            @Nonnull @RequestPart("params") com.example.DownloadWithModelRequestParams params
    ) {
        return this.operations.downloadWithModel(
                file,
                params
        );
    }

    @PostMapping(path = "/downloadWithType", produces = "multipart/form-data", consumes = "multipart/form-data")
    public ResponseEntity<MultiValueMap<String, Object>> downloadWithType(
            @Nonnull @RequestPart("file") org.springframework.core.io.Resource file
    ) {
        return this.operations.downloadWithType(
                file
        );
    }

}
