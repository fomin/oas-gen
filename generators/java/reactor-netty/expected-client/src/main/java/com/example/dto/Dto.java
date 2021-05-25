package com.example.dto;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class Dto implements java.io.Serializable {


    @Nullable
    public final java.lang.String property1;

    public Dto(
            @Nullable java.lang.String property1
    ) {

        this.property1 = property1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.dto.Dto other = (com.example.dto.Dto) o;
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
