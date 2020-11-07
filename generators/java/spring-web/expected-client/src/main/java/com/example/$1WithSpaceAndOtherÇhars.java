package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Property with space and other chars in name
 */
public final class $1WithSpaceAndOtherÇhars {

    /**
     * Property 1
     */
    @Nullable
    @JsonProperty("property1")
    public final java.lang.String property1;

    @JsonCreator
    public $1WithSpaceAndOtherÇhars(
            @Nullable @JsonProperty("property1") java.lang.String property1
    ) {

        this.property1 = property1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.$1WithSpaceAndOtherÇhars other = (com.example.$1WithSpaceAndOtherÇhars) o;
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
        return "$1WithSpaceAndOtherÇhars{" +
                "property1='" + property1 + '\'' +
                '}';
    }
}
