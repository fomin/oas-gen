package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public final class DecimalConverter {

    public static final String INVALID_DECIMAL_ERROR_CODE = "INVALID_DECIMAL";

    private DecimalConverter() {
    }

    public static BigDecimal parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        try {
            return new BigDecimal(textValue);
        } catch (NumberFormatException e) {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_DECIMAL_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            BigDecimal value
    ) throws IOException {
        jsonGenerator.writeNumber(value);
        return Collections.emptyList();
    }

}
