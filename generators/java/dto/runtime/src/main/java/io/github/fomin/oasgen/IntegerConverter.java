package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public final class IntegerConverter {

    public static final String INVALID_INTEGER_ERROR_CODE = "INVALID_INTEGER";

    private IntegerConverter() {
    }

    public static BigInteger parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.NUMBER, jsonNode);
        if (jsonNode.isIntegralNumber()) {
            return jsonNode.bigIntegerValue();
        } else {
            throw new ValidationException(new ValidationError.ValueError(
                    INVALID_INTEGER_ERROR_CODE,
                    jsonNode
            ));
        }
    }

    public static List<? extends ValidationError.ValueError> write(
            JsonGenerator jsonGenerator,
            BigInteger value
    ) throws IOException {
        jsonGenerator.writeNumber(value);
        return Collections.emptyList();
    }

}
