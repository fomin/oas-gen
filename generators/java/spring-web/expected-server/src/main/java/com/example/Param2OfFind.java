package com.example;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;

/**
 * query parameter 2
 */
public enum Param2OfFind {

    VALUE1("value1"),
    VALUE2("value2");

    @Nonnull
    @JsonValue
    public final String strValue;

    Param2OfFind(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static Param2OfFind parseString(String value) {
        switch (value) {
            case "value1":
                return VALUE1;
            case "value2":
                return VALUE2;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

    public static String writeString(Param2OfFind value) {
        return value.strValue;
    }

}
