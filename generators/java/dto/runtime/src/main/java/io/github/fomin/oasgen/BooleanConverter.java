package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class BooleanConverter {

    private BooleanConverter() {
    }

    public static boolean parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.BOOLEAN, jsonNode);
        return jsonNode.booleanValue();
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            boolean value
    ) throws IOException {
        jsonGenerator.writeBoolean(value);
        return Collections.emptyList();
    }

}
