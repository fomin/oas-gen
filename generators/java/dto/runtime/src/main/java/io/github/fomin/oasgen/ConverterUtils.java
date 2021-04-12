package io.github.fomin.oasgen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Collections;

public final class ConverterUtils {
    private ConverterUtils() {
    }

    public static void checkNodeType(JsonNodeType expectedType, JsonNode jsonNode) {
        if (jsonNode.getNodeType() != expectedType) {
            throw new ValidationException(Collections.singletonList(
                    new ValidationError.NodeTypeError(expectedType, jsonNode)
            ));
        }
    }
}
