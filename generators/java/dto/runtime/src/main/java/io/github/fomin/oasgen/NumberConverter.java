package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.math.BigDecimal;

public class NumberConverter {
    public static ScalarParser<BigDecimal> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT,
                JsonParser::getDecimalValue
        );
    }

    public static final Writer<BigDecimal> WRITER = JsonGenerator::writeNumber;

}
