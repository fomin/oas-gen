package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public final class NumberConverter {

    private NumberConverter() {
    }

    public static BigDecimal parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.NUMBER, jsonNode);
        return jsonNode.decimalValue();
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            BigDecimal value
    ) throws IOException {
        jsonGenerator.writeNumber(value);
        return Collections.emptyList();
    }

}
