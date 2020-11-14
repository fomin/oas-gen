package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Component item
 */
public final class ComponentItem {



    @JsonCreator
    public ComponentItem(

    ) {


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.ComponentItem other = (com.example.ComponentItem) o;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(

        );
    }

    @Override
    public String toString() {
        return "ComponentItem{" +

                '}';
    }
}
