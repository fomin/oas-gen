package jsm;

import com.fasterxml.jackson.core.JsonToken;

import java.time.LocalDate;

public class LocalDateConverter {
    public static NonBlockingParser<LocalDate> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                jsonParser -> LocalDate.parse(jsonParser.getText())
        );
    }

    public static final Writer<LocalDate> WRITER =
            (jsonGenerator, localDate) -> jsonGenerator.writeString(localDate.toString());

}
