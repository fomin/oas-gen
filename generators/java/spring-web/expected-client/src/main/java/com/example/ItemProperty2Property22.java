package com.example;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;

/**
 * Property 22
 */
public enum ItemProperty2Property22 {

    VALUE1("value1"),
    VALUE2("value2"),
    VALUE3("value3");

    @Nonnull
    @JsonValue
    public final String strValue;

    ItemProperty2Property22(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static ItemProperty2Property22 of(String value) {
        switch (value) {
            case "value1":
                return VALUE1;
            case "value2":
                return VALUE2;
            case "value3":
                return VALUE3;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

}
