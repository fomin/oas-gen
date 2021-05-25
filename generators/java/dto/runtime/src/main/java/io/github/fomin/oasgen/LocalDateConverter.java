package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public final class LocalDateConverter {

    public static final String INVALID_LOCAL_DATE_ERROR_CODE = "INVALID_LOCAL_DATE";

    private LocalDateConverter() {
    }

    public static LocalDate parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        try {
            return LocalDate.parse(textValue);
        } catch (DateTimeParseException e) {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_LOCAL_DATE_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            LocalDate value
    ) throws IOException {
        jsonGenerator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        return Collections.emptyList();
    }

}
