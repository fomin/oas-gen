package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Int64Converter {
    public static NonBlockingParser<Long> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_NUMBER_INT,
                JsonParser::getLongValue
        );
    }

    public static final Writer<Long> WRITER = JsonGenerator::writeNumber;

}
