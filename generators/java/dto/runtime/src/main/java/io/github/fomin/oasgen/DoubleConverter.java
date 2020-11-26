package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class DoubleConverter {
    public static ScalarParser<Double> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT,
                JsonParser::getDoubleValue
        );
    }

    public static final Writer<Double> WRITER = JsonGenerator::writeNumber;

}
