package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Int32Converter {
    public static NonBlockingParser<Integer> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                JsonParser::getIntValue
        );
    }

    public static final Writer<Integer> WRITER = JsonGenerator::writeNumber;

}
