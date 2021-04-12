package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class DoubleConverter {

    private DoubleConverter() {
    }

    public static double parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.NUMBER, jsonNode);
        return jsonNode.doubleValue();
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            double value
    ) throws IOException {
        jsonGenerator.writeNumber(value);
        return Collections.emptyList();
    }

}
