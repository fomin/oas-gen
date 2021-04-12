package com.example.enumdto;

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

public final class EnumDtoConverter {

    private EnumDtoConverter() {
    }

    public static com.example.enumdto.EnumDto parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.STRING, jsonNode);
        String textValue = jsonNode.textValue();
        return parseString(textValue);
    }

    public static com.example.enumdto.EnumDto parseString(String value) {
        switch (value) {
            case "value1":
                return com.example.enumdto.EnumDto.VALUE1;
            case "value2":
                return com.example.enumdto.EnumDto.VALUE2;
            default:
                throw new ValidationException(new ValidationError.StringValue(
                        EnumConverter.NOT_IN_ENUM_ERROR_CODE, value
                ));
        }
    }

    public static List<? extends ValidationError> write(
            JsonGenerator jsonGenerator,
            com.example.enumdto.EnumDto value
    ) throws IOException {
        jsonGenerator.writeString(value.strValue);
        return Collections.emptyList();
    }

    public static String writeString(com.example.enumdto.EnumDto value) {
        return value.strValue;
    }
}
