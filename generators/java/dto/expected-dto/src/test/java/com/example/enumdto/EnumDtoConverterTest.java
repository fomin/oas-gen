package com.example.enumdto;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.github.fomin.oasgen.DtoTestUtils.assertSuccessfulParsing;
import static io.github.fomin.oasgen.DtoTestUtils.assertSuccessfulWriting;

class EnumDtoConverterTest {

    @Test
    void parse() throws IOException {
        assertSuccessfulParsing(
                EnumDto.VALUE1,
                "\"value1\"",
                EnumDtoConverter::parse
        );
    }

    @Test
    void write() throws IOException {
        assertSuccessfulWriting(
                "\"value1\"",
                EnumDto.VALUE1,
                EnumDtoConverter::write
        );
    }
}
