package io.github.fomin.oasgen;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class SkipValueParserTest {

    @Test
    public void skipString() throws IOException {
        DtoTestUtils.assertSuccessfulParsing(
                null,
                "\"string\"",
                new SkipValueParser()
        );
    }

    @Test
    public void skipNumber() throws IOException {
        DtoTestUtils.assertSuccessfulParsing(
                null,
                "123 ", // TODO remove trailing space
                new SkipValueParser()
        );
    }

    @Test
    public void skipObject() throws IOException {
        DtoTestUtils.assertSuccessfulParsing(
                null,
                "{\"property1\":\"value1\",\"property2\":\"value2\"}",
                new SkipValueParser()
        );
    }

    @Test
    public void skipArray() throws IOException {
        DtoTestUtils.assertSuccessfulParsing(
                null,
                "[\"value1\",\"value2\"]",
                new SkipValueParser()
        );
    }
}
