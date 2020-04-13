package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.time.LocalDate;

public class BooleanConverter {
    public static NonBlockingParser<Boolean> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE,
                JsonParser::getBooleanValue
        );
    }

    public static final Writer<Boolean> WRITER = JsonGenerator::writeBoolean;

}
