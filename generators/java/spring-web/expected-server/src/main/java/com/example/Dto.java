package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;


public final class Dto {


    @Nullable
    @JsonProperty("property1")
    public final java.lang.String property1;

    @JsonCreator
    public Dto(
            @Nullable @JsonProperty("property1") java.lang.String property1
    ) {

        this.property1 = property1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.Dto other = (com.example.Dto) o;
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
        return "Dto{" +
                "property1='" + property1 + '\'' +
                '}';
    }
}
