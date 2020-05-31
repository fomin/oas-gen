package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonToken;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter {
    public static NonBlockingParser<LocalDate> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> LocalDate.parse(jsonParser.getText())
        );
    }

    public static final Writer<LocalDate> WRITER =
            (jsonGenerator, localDate) -> jsonGenerator.writeString(
                    localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            );

}
