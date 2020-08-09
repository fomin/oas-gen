package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Item
 */
public final class Item {

    /**
     * Common property 1
     */
    @Nullable
    public final java.lang.String commonProperty1;

    /**
     * Property 1
     */
    @Nonnull
    public final java.lang.String property1;

    /**
     * Property 2
     */
    @Nonnull
    public final com.example.ItemProperty2 property2;

    /**
     * Decimal property
     */
    @Nullable
    public final java.math.BigDecimal decimalProperty;

    /**
     * Local date time property
     */
    @Nullable
    public final java.time.LocalDateTime localDateTimeProperty;

    /**
     * String array property
     */
    @Nullable
    public final java.util.List<java.lang.String> stringArrayProperty;

    /**
     * Map property
     */
    @Nullable
    public final java.util.Map<java.lang.String, java.math.BigDecimal> mapProperty;

    @JsonCreator
    public Item(
            @Nullable @JsonProperty("commonProperty1") java.lang.String commonProperty1,
            @Nonnull @JsonProperty("property1") java.lang.String property1,
            @Nonnull @JsonProperty("property2") com.example.ItemProperty2 property2,
            @Nullable @JsonProperty("decimalProperty") java.math.BigDecimal decimalProperty,
            @Nullable @JsonProperty("localDateTimeProperty") @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING) java.time.LocalDateTime localDateTimeProperty,
            @Nullable @JsonProperty("stringArrayProperty") java.util.List<java.lang.String> stringArrayProperty,
            @Nullable @JsonProperty("mapProperty") java.util.Map<java.lang.String, java.math.BigDecimal> mapProperty
    ) {
        if (property1 == null) {
            throw new NullPointerException("property1 must be not null");
        }
        if (property2 == null) {
            throw new NullPointerException("property2 must be not null");
        }
        this.commonProperty1 = commonProperty1;
        this.property1 = property1;
        this.property2 = property2;
        this.decimalProperty = decimalProperty;
        this.localDateTimeProperty = localDateTimeProperty;
        this.stringArrayProperty = stringArrayProperty;
        this.mapProperty = mapProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.example.Item other = (com.example.Item) o;
        return Objects.equals(commonProperty1, other.commonProperty1) &&
                Objects.equals(property1, other.property1) &&
                Objects.equals(property2, other.property2) &&
                Objects.equals(decimalProperty, other.decimalProperty) &&
                Objects.equals(localDateTimeProperty, other.localDateTimeProperty) &&
                Objects.equals(stringArrayProperty, other.stringArrayProperty) &&
                Objects.equals(mapProperty, other.mapProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                commonProperty1,
                property1,
                property2,
                decimalProperty,
                localDateTimeProperty,
                stringArrayProperty,
                mapProperty
        );
    }

    @Override
    public String toString() {
        return "Item{" +
                "commonProperty1='" + commonProperty1 + '\'' +
                ", property1='" + property1 + '\'' +
                ", property2='" + property2 + '\'' +
                ", decimalProperty='" + decimalProperty + '\'' +
                ", localDateTimeProperty='" + localDateTimeProperty + '\'' +
                ", stringArrayProperty='" + stringArrayProperty + '\'' +
                ", mapProperty='" + mapProperty + '\'' +
                '}';
    }
}
