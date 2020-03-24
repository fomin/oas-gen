package jsm;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Int64Converter {
    public static NonBlockingParser<Long> createParser() {
        return new ScalarParser<>(
                token -> token == JsonToken.VALUE_STRING,
                JsonParser::getLongValue
        );
    }

    public static final Writer<Long> WRITER = JsonGenerator::writeNumber;

}
