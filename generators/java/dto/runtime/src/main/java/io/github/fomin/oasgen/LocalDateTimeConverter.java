package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter {
    public static NonBlockingParser<LocalDateTime> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> LocalDateTime.parse(jsonParser.getText())
        );
    }

    public static final Writer<LocalDateTime> WRITER =
            (jsonGenerator, localDateTime) -> jsonGenerator.writeString(
                    localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );

}
