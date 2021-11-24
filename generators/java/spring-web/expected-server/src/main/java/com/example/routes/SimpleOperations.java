package com.example.routes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SimpleOperations {
    java.lang.String simplePost(
            @Nonnull com.example.dto.Dto dto
    );

    com.example.dto.Dto simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.dto.Param2OfSimpleGet param2,
            @Nullable java.time.LocalDate param3Header
    );

    void testNullableParameter(
            @Nullable java.time.LocalDate param1
    );

    void returnOctetStream(
            @Nonnull java.io.OutputStream outputStream
    ) throws java.io.IOException;

    void sendOctetStream(
            @Nonnull java.io.InputStream inputStream
    ) throws java.io.IOException;
}
