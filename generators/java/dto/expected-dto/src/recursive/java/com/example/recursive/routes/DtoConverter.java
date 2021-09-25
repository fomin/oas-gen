package com.example.recursive.routes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.fomin.oasgen.ConverterUtils;
import io.github.fomin.oasgen.ValidationError;
import io.github.fomin.oasgen.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class DtoConverter {

    private DtoConverter() {
    }

    public static com.example.recursive.dto.Dto parse(JsonNode jsonNode) {
        ConverterUtils.checkNodeType(JsonNodeType.OBJECT, jsonNode);
        com.example.recursive.dto.Dto p0 = null; // recursiveProperty
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        List<ValidationError.ObjectFieldError> errors = null;
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            try {
                switch (key) {
                    case "recursiveProperty":
                        p0 = com.example.recursive.routes.DtoConverter.parse(value);
                        break;
                }
            } catch (ValidationException e) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(new ValidationError.ObjectFieldError(
                        key,
                        e.validationErrors
                ));
            }
        }
        if (errors != null) {
            throw new ValidationException(errors);
        }
        return new com.example.recursive.dto.Dto(p0);
    }

    public static List<? extends ValidationError> write(JsonGenerator jsonGenerator, com.example.recursive.dto.Dto value) throws IOException {
        List<ValidationError.ObjectFieldError> errors = null;
        jsonGenerator.writeStartObject();
        if (value.recursiveProperty != null) {
            jsonGenerator.writeFieldName("recursiveProperty");
            List<? extends ValidationError> validationErrors = com.example.recursive.routes.DtoConverter.write(jsonGenerator, value.recursiveProperty);
            if (!validationErrors.isEmpty()) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(new ValidationError.ObjectFieldError(
                        "recursiveProperty",
                        validationErrors
                ));
            }
        }
        jsonGenerator.writeEndObject();
        if (errors != null) {
            return errors;
        } else {
            return Collections.emptyList();
        }
    }
}
