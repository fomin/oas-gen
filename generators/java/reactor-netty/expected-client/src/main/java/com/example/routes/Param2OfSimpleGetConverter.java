package com.example.routes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.fomin.oasgen.ConverterUtils;
import io.github.fomin.oasgen.EnumConverter;
import io.github.fomin.oasgen.ValidationError;
import io.github.fomin.oasgen.ValidationException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class Param2OfSimpleGetConverter {

    private Param2OfSimpleGetConverter() {
    }

    public static com.example.dto.Param2OfSimpleGet parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        return parseString(textValue);
    }

    public static com.example.dto.Param2OfSimpleGet parseString(String value) {
        switch (value) {
            case "value1":
                return com.example.dto.Param2OfSimpleGet.VALUE1;
            case "value2":
                return com.example.dto.Param2OfSimpleGet.VALUE2;
            default:
                throw new ValidationException(new ValidationError.StringValue(
                        EnumConverter.NOT_IN_ENUM_ERROR_CODE, value
                ));
        }
    }

    public static List<? extends ValidationError> write(
            JsonGenerator jsonGenerator,
            com.example.dto.Param2OfSimpleGet value
    ) throws IOException {
        jsonGenerator.writeString(value.strValue);
        return Collections.emptyList();
    }

    public static String writeString(com.example.dto.Param2OfSimpleGet value) {
        return value.strValue;
    }
}
