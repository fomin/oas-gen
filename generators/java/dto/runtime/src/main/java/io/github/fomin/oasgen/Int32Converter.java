package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class Int32Converter {

    public static final String INVALID_INT_32_ERROR_CODE = "INVALID_INT32";

    private Int32Converter() {
    }

    public static int parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.NUMBER, jsonNode);
        if (jsonNode.isIntegralNumber() && jsonNode.canConvertToInt()) {
            return jsonNode.intValue();
        } else {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_INT_32_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            int value
    ) throws IOException {
        jsonGenerator.writeNumber(value);
        return Collections.emptyList();
    }

}
