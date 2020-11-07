package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Schema with reserved word in name
 */
public final class True {

    /**
     * Property 1
     */
    @Nullable
    @JsonProperty("property1")
    public final java.lang.String property1;

    @JsonCreator
    public True(
            @Nullable @JsonProperty("property1") java.lang.String property1
    ) {

        this.property1 = property1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.True other = (com.example.True) o;
        return Objects.equals(property1, other.property1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                property1
        );
    }

    @Override
    public String toString() {
        return "True{" +
                "property1='" + property1 + '\'' +
                '}';
    }
}
