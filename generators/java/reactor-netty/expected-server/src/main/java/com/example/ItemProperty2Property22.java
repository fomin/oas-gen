package com.example;

import javax.annotation.Nonnull;

/**
 * Property 22
 */
public enum ItemProperty2Property22 {
    VALUE1("value1"),
    VALUE2("value2"),
    VALUE3("value3");

    @Nonnull
    public final String strValue;

    ItemProperty2Property22(@Nonnull String strValue) {
        this.strValue = strValue;
    }

    public static io.github.fomin.oasgen.NonBlockingParser<com.example.ItemProperty2Property22> createParser() {
        return new io.github.fomin.oasgen.ScalarParser<>(
                token -> token == com.fasterxml.jackson.core.JsonToken.VALUE_STRING,
                jsonParser -> {
                    String value = jsonParser.getText();
                    return parseString(value);
                }
        );
    }

    public static final io.github.fomin.oasgen.Writer<com.example.ItemProperty2Property22> WRITER =
            (jsonGenerator, value) -> jsonGenerator.writeString(value.strValue);

    public static com.example.ItemProperty2Property22 parseString(String value) {
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

    public static String writeString(com.example.ItemProperty2Property22 value) {
        return value.strValue;
    }

}
