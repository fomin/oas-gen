package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public final class OffsetDateTimeConverter {

    public static final String INVALID_DATE_TIME_ERROR_CODE = "INVALID_DATE_TIME";

    private OffsetDateTimeConverter() {
    }

    public static OffsetDateTime parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        try {
            return OffsetDateTime.parse(textValue);
        } catch (ValidationException e) {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_DATE_TIME_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static List<? extends ValidationError> write(
            JsonGenerator jsonGenerator,
            OffsetDateTime value
    ) throws IOException {
        jsonGenerator.writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        return Collections.emptyList();
    }

}
