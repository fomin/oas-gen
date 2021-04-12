package com.example;

import javax.annotation.Nonnull;

/**
 * Dto title
 *
 * <p>Dto description</p>
 */
public enum Param2OfSimpleGet {
    VALUE1("value1"),
    VALUE2("value2");

    @Nonnull
    public final String strValue;

    Param2OfSimpleGet(String strValue) {
        this.strValue = strValue;
    }
}
