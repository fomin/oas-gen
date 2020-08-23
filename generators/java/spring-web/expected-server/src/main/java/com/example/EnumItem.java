package com.example;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;

/**
 * enum item
 */
public enum EnumItem {

    VALUE_1("value_1"),
    VALUE_2("value_2");

    @Nonnull
    @JsonValue
    public final String strValue;

    EnumItem(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static EnumItem of(String value) {
        switch (value) {
            case "value_1":
                return VALUE_1;
            case "value_2":
                return VALUE_2;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

}
