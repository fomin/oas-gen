package com.example;

/**
 * Property 22
 */
public enum ItemProperty2Property22 {
    VALUE1("value1"),
    VALUE2("value2"),
    VALUE3("value3");

    public final String strValue;

    ItemProperty2Property22(String strValue) {
        this.strValue = strValue;
    }

    public static jsm.NonBlockingParser<com.example.ItemProperty2Property22> createParser() {
        return new jsm.ScalarParser<>(
                token -> token == com.fasterxml.jackson.core.JsonToken.VALUE_STRING,
                jsonParser -> {
                    String value = jsonParser.getText();
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
        );
    }

    public static final jsm.Writer<com.example.ItemProperty2Property22> WRITER =
            (jsonGenerator, value) -> jsonGenerator.writeString(value.strValue);

}
