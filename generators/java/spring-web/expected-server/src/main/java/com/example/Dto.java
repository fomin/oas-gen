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


    @Nullable
    @JsonProperty("property2")
    public final com.example.DtoProperty2 property2;


    @Nullable
    @JsonProperty("property3")
    public final com.example.DtoProperty3 property3;

    @JsonCreator
    public Dto(
            @Nullable @JsonProperty("property1") java.lang.String property1,
            @Nullable @JsonProperty("property2") com.example.DtoProperty2 property2,
            @Nullable @JsonProperty("property3") com.example.DtoProperty3 property3
    ) {

        this.property1 = property1;
        this.property2 = property2;
        this.property3 = property3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.Dto other = (com.example.Dto) o;
        return Objects.equals(property1, other.property1) &&
                Objects.equals(property2, other.property2) &&
                Objects.equals(property3, other.property3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                property1,
                property2,
                property3
        );
    }

    @Override
    public String toString() {
        return "Dto{" +
                "property1='" + property1 + '\'' +
                ", property2='" + property2 + '\'' +
                ", property3='" + property3 + '\'' +
                '}';
    }
}
