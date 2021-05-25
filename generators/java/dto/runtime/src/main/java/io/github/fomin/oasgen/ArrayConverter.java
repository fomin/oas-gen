package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ArrayConverter {

    private ArrayConverter() {
    }

    public static <T> List<T> parse(JsonNode jsonNode, Function<JsonNode, T> itemParser) {
        ConverterUtils.checkNodeType(JsonNodeType.ARRAY, jsonNode);
        List<T> parsedItems = new ArrayList<>(jsonNode.size());
        List<ValidationError.ArrayItemError> errors = null;
        for (int i = 0; i < jsonNode.size(); i++) {
            JsonNode itemNode = jsonNode.get(i);
            T parsedItem;
            try {
                parsedItem = itemParser.apply(itemNode);
            } catch (ValidationException e) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(
                        new ValidationError.ArrayItemError(i, e.validationErrors)
                );
                continue;
            }
            parsedItems.add(parsedItem);
        }
        if (errors != null) {
            throw new ValidationException(errors);
        } else {
            return parsedItems;
        }
    }

    public static <T> List<? extends ValidationError> write(
            JsonGenerator jsonGenerator,
            ValueWriter<T> itemWriter,
            List<T> items
    ) throws IOException {
        List<ValidationError.ArrayItemError> errors = null;
        jsonGenerator.writeStartArray();
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            List<? extends ValidationError> itemErrors = itemWriter.write(jsonGenerator, item);
            if (!itemErrors.isEmpty()) {
                if (errors == null) {
                    errors = new ArrayList<>();
                }
                errors.add(new ValidationError.ArrayItemError(i, itemErrors));
            }
        }
        jsonGenerator.writeEndArray();
        if (errors == null) {
            return Collections.emptyList();
        } else {
            return errors;
        }
    }

}
