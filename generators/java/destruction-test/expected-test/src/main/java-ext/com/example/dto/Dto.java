package com.example.dto;

import javax.annotation.Nullable;
import java.util.Objects;


public final class Dto implements java.io.Serializable {


    @Nullable
    public final String property1;

    public Dto(
            @Nullable String property1
    ) {

        this.property1 = property1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dto other = (Dto) o;
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
