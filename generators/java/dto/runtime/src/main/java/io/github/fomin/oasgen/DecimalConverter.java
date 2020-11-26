package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;

import java.math.BigDecimal;

public class DecimalConverter {
    public static ScalarParser<BigDecimal> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> new BigDecimal(jsonParser.getText())
        );
    }

    public static final Writer<BigDecimal> WRITER = (jsonGenerator, v) -> jsonGenerator.writeString(v.toPlainString());

}
