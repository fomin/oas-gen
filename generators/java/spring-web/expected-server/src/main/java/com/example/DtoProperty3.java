package com.example;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;


public enum DtoProperty3 {

    ONE(1),
    TWO(2);

    @Nonnull
    @JsonValue
    public final Integer intValue; 

    DtoProperty3(@Nonnull Integer intValue) {
        this.intValue = intValue;
    }

    public static DtoProperty3 parseInteger(Integer value) {
        switch (value) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

    public static Integer writeString(DtoProperty3 value) {
        return value.intValue;
    }

}
