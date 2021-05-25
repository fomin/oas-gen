package com.example.javadoc.dto;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Dto title
 *
 * <p>Dto description</p>
 */
public final class Dto implements java.io.Serializable {

    /**
     * Property 1 title
     *
     * <p>Property 1 description</p>
     */
    @Nullable
    public final java.lang.String property1;
    /**
     * Property 2 title
     */
    @Nullable
    public final java.lang.String property2;

    public Dto(
            @Nullable java.lang.String property1,
            @Nullable java.lang.String property2
    ) {

        this.property1 = property1;
        this.property2 = property2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.javadoc.dto.Dto other = (com.example.javadoc.dto.Dto) o;
        return Objects.equals(property1, other.property1) &&
                Objects.equals(property2, other.property2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                property1,
                property2
        );
    }

    @Override
    public String toString() {
        return "Dto{" +
                "property1='" + property1 + '\'' +
                ", property2='" + property2 + '\'' +
                '}';
    }

}
