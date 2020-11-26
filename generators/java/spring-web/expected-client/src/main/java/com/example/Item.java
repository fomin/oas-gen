package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Item
 *
 * <p>This is a very long <em>description</em> of item.
 * This is a very long <em>description</em> of item.</p>
 * <p>This is a very long <em>description</em> of item.
 * This is a very long <em>description</em> of item.</p>
 */
public final class Item {

    /**
     * Common property 1
     */
    @Nullable
    @JsonProperty("commonProperty1")
    public final java.lang.String commonProperty1;

    /**
     * Property 1
     *
     * <p>This is a very long <em>description</em> of property 1
     * This is a very long <em>description</em> of property 1</p>
     * <p>This is a very long <em>description</em> of property 1
     * This is a very long <em>description</em> of property 1</p>
     */
    @Nonnull
    @JsonProperty("property1")
    public final java.lang.String property1;

    /**
     * Property 2
     */
    @Nonnull
    @JsonProperty("property2")
    public final com.example.ItemProperty2 property2;

    /**
     * Decimal property
     */
    @Nullable
    @JsonProperty("decimalProperty")
    public final java.math.BigDecimal decimalProperty;

    /**
     * Local date time property
     */
    @Nullable
    @JsonProperty("localDateTimeProperty")
    public final java.time.LocalDateTime localDateTimeProperty;

    /**
     * String array property
     */
    @Nullable
    @JsonProperty("stringArrayProperty")
    public final java.util.List<java.lang.String> stringArrayProperty;

    /**
     * Date-time array property
     */
    @Nullable
    @JsonProperty("dateTimeArrayProperty")
    public final java.util.List<java.time.OffsetDateTime> dateTimeArrayProperty;

    /**
     * Map property
     */
    @Nullable
    @JsonProperty("mapProperty")
    public final java.util.Map<java.lang.String, java.lang.Double> mapProperty;

    /**
     * Date-time map property
     */
    @Nullable
    @JsonProperty("dateTimeMapProperty")
    public final java.util.Map<java.lang.String, java.time.OffsetDateTime> dateTimeMapProperty;

    /**
     * Schema with reserved word in name
     */
    @Nonnull
    @JsonProperty("true")
    public final com.example.True true$;

    /**
     * Property with space and other chars in name
     */
    @Nonnull
    @JsonProperty("1 with space-and+other_çhars")
    public final com.example.$1WithSpaceAndOtherÇhars $1WithSpaceAndOtherÇhars;

    @JsonCreator
    public Item(
            @Nullable @JsonProperty("commonProperty1") java.lang.String commonProperty1,
            @Nonnull @JsonProperty("property1") java.lang.String property1,
            @Nonnull @JsonProperty("property2") com.example.ItemProperty2 property2,
            @Nullable @JsonProperty("decimalProperty") java.math.BigDecimal decimalProperty,
            @Nullable @JsonProperty("localDateTimeProperty") @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING) java.time.LocalDateTime localDateTimeProperty,
            @Nullable @JsonProperty("stringArrayProperty") java.util.List<java.lang.String> stringArrayProperty,
            @Nullable @JsonProperty("dateTimeArrayProperty") java.util.List<java.time.OffsetDateTime> dateTimeArrayProperty,
            @Nullable @JsonProperty("mapProperty") java.util.Map<java.lang.String, java.lang.Double> mapProperty,
            @Nullable @JsonProperty("dateTimeMapProperty") java.util.Map<java.lang.String, java.time.OffsetDateTime> dateTimeMapProperty,
            @Nonnull @JsonProperty("true") com.example.True true$,
            @Nonnull @JsonProperty("1 with space-and+other_çhars") com.example.$1WithSpaceAndOtherÇhars $1WithSpaceAndOtherÇhars
    ) {
        if (property1 == null) {
            throw new NullPointerException("property1 must be not null");
        }
        if (property2 == null) {
            throw new NullPointerException("property2 must be not null");
        }
        if (true$ == null) {
            throw new NullPointerException("true$ must be not null");
        }
        if ($1WithSpaceAndOtherÇhars == null) {
            throw new NullPointerException("$1WithSpaceAndOtherÇhars must be not null");
        }
        this.commonProperty1 = commonProperty1;
        this.property1 = property1;
        this.property2 = property2;
        this.decimalProperty = decimalProperty;
        this.localDateTimeProperty = localDateTimeProperty;
        this.stringArrayProperty = stringArrayProperty;
        this.dateTimeArrayProperty = dateTimeArrayProperty;
        this.mapProperty = mapProperty;
        this.dateTimeMapProperty = dateTimeMapProperty;
        this.true$ = true$;
        this.$1WithSpaceAndOtherÇhars = $1WithSpaceAndOtherÇhars;
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
                Objects.equals(dateTimeArrayProperty, other.dateTimeArrayProperty) &&
                Objects.equals(mapProperty, other.mapProperty) &&
                Objects.equals(dateTimeMapProperty, other.dateTimeMapProperty) &&
                Objects.equals(true$, other.true$) &&
                Objects.equals($1WithSpaceAndOtherÇhars, other.$1WithSpaceAndOtherÇhars);
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
                dateTimeArrayProperty,
                mapProperty,
                dateTimeMapProperty,
                true$,
                $1WithSpaceAndOtherÇhars
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
                ", dateTimeArrayProperty='" + dateTimeArrayProperty + '\'' +
                ", mapProperty='" + mapProperty + '\'' +
                ", dateTimeMapProperty='" + dateTimeMapProperty + '\'' +
                ", true$='" + true$ + '\'' +
                ", $1WithSpaceAndOtherÇhars='" + $1WithSpaceAndOtherÇhars + '\'' +
                '}';
    }
}
