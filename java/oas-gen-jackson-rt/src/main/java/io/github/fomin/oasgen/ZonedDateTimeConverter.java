package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;

import java.time.ZonedDateTime;

public class ZonedDateTimeConverter {
    public static NonBlockingParser<ZonedDateTime> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> ZonedDateTime.parse(jsonParser.getText())
        );
    }

    public static final Writer<ZonedDateTime> WRITER =
            (jsonGenerator, zonedDateTime) -> jsonGenerator.writeString(zonedDateTime.toString());

}
