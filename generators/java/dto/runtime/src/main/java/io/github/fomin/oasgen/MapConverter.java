package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class MapConverter {

    private MapConverter() {
    }

    public static <T> Map<String, T> parse(JsonNode jsonNode, Function<JsonNode, T> valueParser) {
        ConverterUtils.checkNodeType(JsonNodeType.OBJECT, jsonNode);
        Map<String, T> map = new LinkedHashMap<>(jsonNode.size());
        List<ValidationError.ObjectFieldError> errors = null;
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
            String fieldName = fieldEntry.getKey();
            JsonNode fieldValue = fieldEntry.getValue();
            T parsedValue;
            try {
                parsedValue = valueParser.apply(fieldValue);
                map.put(fieldName, parsedValue);
            } catch (ValidationException e) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(new ValidationError.ObjectFieldError(
                        fieldName,
                        e.validationErrors
                ));
            }
        }
        if (errors != null) {
            throw new ValidationException(errors);
        } else {
            return map;
        }
    }

    public static <T> List<? extends ValidationError> write(
            JsonGenerator jsonGenerator,
            ValueWriter<T> valueWriter,
            Map<String, T> map
    ) throws IOException {
        List<ValidationError.ObjectFieldError> errors = null;
        jsonGenerator.writeStartObject();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            jsonGenerator.writeFieldName(key);
            List<? extends ValidationError> valueErrors = valueWriter.write(jsonGenerator, value);
            if (!valueErrors.isEmpty()) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(new ValidationError.ObjectFieldError(key, valueErrors));
            }
        }
        jsonGenerator.writeEndObject();
        if (errors == null) {
            return Collections.emptyList();
        } else {
            return errors;
        }
    }
}
