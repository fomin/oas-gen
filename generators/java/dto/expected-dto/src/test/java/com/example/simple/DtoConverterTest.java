package com.example.simple;

import com.example.simple.dto.Dto;
import com.example.simple.routes.DtoConverter;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.github.fomin.oasgen.ValidationError;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.github.fomin.oasgen.DtoTestUtils.*;
import static java.util.Collections.singletonList;

class DtoConverterTest {

    @Test
    public void successfulParserTest() throws IOException {
        assertSuccessfulParsing(
                new Dto("value1", "value2"),
                "{\"property1\":\"value1\",\"property2\":\"value2\"}",
                DtoConverter::parse
        );
    }

    @Test
    public void extraPropertiesParserTest() throws IOException {
        assertSuccessfulParsing(
                new Dto("value1", "value2"),
                "{\"property1\":\"value1\",\"property2\":\"value2\",\"property3\":\"value3\"}",
                DtoConverter::parse
        );
    }

    @Test
    public void propertyWithWrongType() throws IOException {
        assertParsingValidationErrors(
                singletonList(
                        new ValidationError.ObjectFieldError(
                                "property1",
                                singletonList(
                                        new ValidationError.NodeTypeError(JsonNodeType.STRING, IntNode.valueOf(1))
                                )
                        )
                ),
                "{\"property1\":1}",
                DtoConverter::parse
        );
    }

    @Test
    public void normalWriterTest() throws IOException {
        assertSuccessfulWriting(
                "{\"property1\":\"value1\",\"property2\":\"value2\"}",
                new Dto("value1", "value2"),
                DtoConverter::write
        );
    }
}
