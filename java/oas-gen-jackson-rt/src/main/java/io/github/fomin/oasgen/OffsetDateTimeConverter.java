package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;

import java.time.OffsetDateTime;

public class OffsetDateTimeConverter {
    public static NonBlockingParser<OffsetDateTime> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> OffsetDateTime.parse(jsonParser.getText())
        );
    }

    public static final Writer<OffsetDateTime> WRITER =
            (jsonGenerator, offsetDateTime) -> jsonGenerator.writeString(offsetDateTime.toString());

}
