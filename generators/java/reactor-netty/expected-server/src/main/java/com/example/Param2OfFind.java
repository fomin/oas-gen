package com.example;

import javax.annotation.Nonnull;

/**
 * query parameter 2
 */
public enum Param2OfFind {
    VALUE1("value1"),
    VALUE2("value2");

    @Nonnull
    public final String strValue;

    Param2OfFind(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static io.github.fomin.oasgen.NonBlockingParser<com.example.Param2OfFind> createParser() {
        return new io.github.fomin.oasgen.ScalarParser<>(
                token -> token == com.fasterxml.jackson.core.JsonToken.VALUE_STRING,
                jsonParser -> {
                    String value = jsonParser.getText();
                    return parseString(value);
                }
        );
    }

    public static final io.github.fomin.oasgen.Writer<com.example.Param2OfFind> WRITER =
            (jsonGenerator, value) -> jsonGenerator.writeString(value.strValue);

    public static com.example.Param2OfFind parseString(String value) {
        switch (value) {
            case "value1":
                return VALUE1;
            case "value2":
                return VALUE2;
            default:
                throw new UnsupportedOperationException("Unsupported value " + value);
        }
    }

    public static String writeString(com.example.Param2OfFind value) {
        return value.strValue;
    }

}
