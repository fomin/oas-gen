package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ScalarWriter {

    public static final Writer<String> STRING_WRITER = JsonGenerator::writeString;

    public static final Writer<LocalDateTime> STRING_LOCAL_DATE_TIME_WRITER =
            (jsonGenerator, localDateTime) -> jsonGenerator.writeString(localDateTime.toString());

    public static final Writer<BigDecimal> NUMBER_WRITER = JsonGenerator::writeNumber;

}
