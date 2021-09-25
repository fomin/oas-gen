package com.example.recursive.dto;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public final class Dto implements java.io.Serializable {


    @Nullable
    public final com.example.recursive.dto.Dto recursiveProperty;

    public Dto(
            @Nullable com.example.recursive.dto.Dto recursiveProperty
    ) {

        this.recursiveProperty = recursiveProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.recursive.dto.Dto other = (com.example.recursive.dto.Dto) o;
        return Objects.equals(recursiveProperty, other.recursiveProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                recursiveProperty
        );
    }

    @Override
    public String toString() {
        return "Dto{" +
                "recursiveProperty='" + recursiveProperty + '\'' +
                '}';
    }

}
