package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class StringConverter {

    public static ScalarParser<String> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                JsonParser::getText
        );
    }

    public static final Writer<String> WRITER = JsonGenerator::writeString;

}
