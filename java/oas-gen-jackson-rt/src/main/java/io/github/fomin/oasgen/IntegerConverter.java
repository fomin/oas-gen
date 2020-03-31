package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.math.BigInteger;

public class IntegerConverter {
    public static NonBlockingParser<BigInteger> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_NUMBER_INT,
                JsonParser::getBigIntegerValue
        );
    }

    public static final Writer<BigInteger> WRITER = JsonGenerator::writeNumber;

}
