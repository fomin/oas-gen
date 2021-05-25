package com.example.enumdto.dto;

import javax.annotation.Nonnull;

/**
 * Dto title
 *
 * <p>Dto description</p>
 */
public enum EnumDto {
    VALUE1("value1"),
    VALUE2("value2");

    @Nonnull
    public final String strValue;

    EnumDto(String strValue) {
        this.strValue = strValue;
    }
}
