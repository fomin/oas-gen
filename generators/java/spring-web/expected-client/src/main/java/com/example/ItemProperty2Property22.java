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

}
