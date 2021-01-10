package com.example;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;


public enum Param2OfSimpleGet {

    VALUE1("value1"),
    VALUE2("value2");

    @Nonnull
    @JsonValue
    public final String strValue;

    Param2OfSimpleGet(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static Param2OfSimpleGet parseString(String value) {
        switch (value) {
            case "value1":
                return VALUE1;
            case "value2":
                return VALUE2;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

    public static String writeString(Param2OfSimpleGet value) {
        return value.strValue;
    }

}
