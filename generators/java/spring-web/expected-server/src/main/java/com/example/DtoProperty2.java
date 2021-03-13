package com.example;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;


public enum DtoProperty2 {

    ACCEPT("ACCEPT"),
    DEFAULT("DEFAULT");

    @Nonnull
    @JsonValue
    public final String strValue; 

    DtoProperty2(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static DtoProperty2 parseString(String value) {
        switch (value) {
            case "ACCEPT":
                return ACCEPT;
            case "DEFAULT":
                return DEFAULT;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

    public static String writeString(DtoProperty2 value) {
        return value.strValue;
    }

}
