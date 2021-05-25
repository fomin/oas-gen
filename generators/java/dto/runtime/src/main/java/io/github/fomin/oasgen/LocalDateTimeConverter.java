package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public final class LocalDateTimeConverter {

    public static final String INVALID_LOCAL_DATE_TIME_ERROR_CODE = "INVALID_LOCAL_DATE_TIME";

    private LocalDateTimeConverter() {
    }

    public static LocalDateTime parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        try {
            return LocalDateTime.parse(textValue);
        } catch (DateTimeParseException e) {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_LOCAL_DATE_TIME_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static LocalDateTime parse(JsonNode jsonNode, String pattern) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatterCache.get(pattern);
        try {
            return LocalDateTime.parse(textValue, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_LOCAL_DATE_TIME_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            LocalDateTime value
    ) throws IOException {
        jsonGenerator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return Collections.emptyList();
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            String pattern,
            LocalDateTime value
    ) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatterCache.get(pattern);
        jsonGenerator.writeString(value.format(dateTimeFormatter));
        return Collections.emptyList();
    }
}
