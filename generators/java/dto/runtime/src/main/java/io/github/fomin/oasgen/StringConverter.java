package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class StringConverter {
    private StringConverter() {
    }

    public static String parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        return jsonNode.textValue();
    }

    public static List<? extends ValidationError> write(JsonGenerator jsonGenerator, String value) throws IOException {
        jsonGenerator.writeString(value);
        return Collections.emptyList();
    }

}
