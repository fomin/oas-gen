package com.example.simple;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.github.fomin.oasgen.DtoTestUtils.assertSuccessfulParsing;
import static io.github.fomin.oasgen.DtoTestUtils.assertSuccessfulWriting;

class DtoTest {

    @Test
    public void successfulParserTest() throws IOException {
        assertSuccessfulParsing(
                new Dto("value1", "value2"),
                "{\"property1\":\"value1\",\"property2\":\"value2\"}",
                new Dto.Parser()
        );
    }

    @Test
    public void extraPropertiesParserTest() throws IOException {
        assertSuccessfulParsing(
                new Dto("value1", "value2"),
                "{\"property1\":\"value1\",\"property2\":\"value2\",\"property3\":\"value3\"}",
                new Dto.Parser()
        );
    }

    @Test
    public void normalWriterTest() throws IOException {
        assertSuccessfulWriting(
                "{\"property1\":\"value1\",\"property2\":\"value2\"}",
                new Dto("value1", "value2"),
                Dto.Writer.INSTANCE
        );
    }
}
