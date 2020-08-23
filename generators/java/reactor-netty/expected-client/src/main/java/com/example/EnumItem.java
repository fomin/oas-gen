package com.example;

import javax.annotation.Nonnull;

/**
 * enum item
 */
public enum EnumItem {
    VALUE_1("value_1"),
    VALUE_2("value_2");

    @Nonnull
    public final String strValue;

    EnumItem(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static io.github.fomin.oasgen.NonBlockingParser<com.example.EnumItem> createParser() {
        return new io.github.fomin.oasgen.ScalarParser<>(
                token -> token == com.fasterxml.jackson.core.JsonToken.VALUE_STRING,
                jsonParser -> of(jsonParser.getText())
        );
    }

    public static final io.github.fomin.oasgen.Writer<com.example.EnumItem> WRITER =
            (jsonGenerator, value) -> jsonGenerator.writeString(value.strValue);

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
