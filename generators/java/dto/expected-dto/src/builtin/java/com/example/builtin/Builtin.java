package com.example.builtin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class Builtin {

    public static java.math.BigDecimal parseDecimal(JsonNode jsonNode) {
        return io.github.fomin.oasgen.DecimalConverter.parse(jsonNode);
    }

    public static void writeDecimal(JsonGenerator jsonGenerator, java.math.BigDecimal value) throws IOException {
        io.github.fomin.oasgen.DecimalConverter.write(jsonGenerator, value);
    }

    public static java.lang.Integer parseInt32(JsonNode jsonNode) {
        return io.github.fomin.oasgen.Int32Converter.parse(jsonNode);
    }

    public static void writeInt32(JsonGenerator jsonGenerator, java.lang.Integer value) throws IOException {
        io.github.fomin.oasgen.Int32Converter.write(jsonGenerator, value);
    }

}
