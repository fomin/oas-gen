package com.example;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SimpleOperations {
    java.lang.String simplePost(
            @Nonnull com.example.Dto dto
    );

    com.example.Dto simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.Param2OfSimpleGet param2
    );
}
