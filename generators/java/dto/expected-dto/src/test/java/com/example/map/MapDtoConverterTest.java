package com.example.map;

import io.github.fomin.oasgen.MapConverter;
import io.github.fomin.oasgen.NumberConverter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.fomin.oasgen.DtoTestUtils.assertSuccessfulParsing;
import static io.github.fomin.oasgen.DtoTestUtils.assertSuccessfulWriting;

class MapDtoConverterTest {

    @Test
    void parse() throws IOException {
        Map<String, BigDecimal> expectedMap = new LinkedHashMap<>();
        expectedMap.put("value1", new BigDecimal("1.0"));
        expectedMap.put("value2", new BigDecimal("2.0"));
        assertSuccessfulParsing(
                expectedMap,
                "{\"value1\":1.0,\"value2\":2.0}",
                jsonNode -> MapConverter.parse(jsonNode, NumberConverter::parse)
        );
    }

    @Test
    void write() throws IOException {
        Map<String, BigDecimal> expectedMap = new LinkedHashMap<>();
        expectedMap.put("value1", new BigDecimal("1.0"));
        expectedMap.put("value2", new BigDecimal("2.0"));
        assertSuccessfulWriting(
                "{\"value1\":1.0,\"value2\":2.0}",
                expectedMap,
                (jsonGenerator, map) -> MapConverter.write(jsonGenerator, NumberConverter::write, map)
        );
    }
}
