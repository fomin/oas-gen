package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Property 2
 */
public final class ItemProperty2 {

    /**
     * Common property 1
     */
    @Nullable
    public final java.lang.String commonProperty1;

    /**
     * Property 21
     */
    @Nullable
    public final java.lang.String property21;

    /**
     * Property 22
     */
    @Nullable
    public final com.example.ItemProperty2Property22 property22;

    @JsonCreator
    public ItemProperty2(
            @Nullable @JsonProperty("commonProperty1") java.lang.String commonProperty1,
            @Nullable @JsonProperty("property21") java.lang.String property21,
            @Nullable @JsonProperty("property22") com.example.ItemProperty2Property22 property22
    ) {

        this.commonProperty1 = commonProperty1;
        this.property21 = property21;
        this.property22 = property22;
    }
}
