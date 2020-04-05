package com.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import io.github.fomin.oasgen.NonBlockingParser;
import io.github.fomin.oasgen.ObjectParserState;
import io.github.fomin.oasgen.ParseResult;
import java.io.IOException;

/**
 * Item
 */
public final class Item {

    /**
     * Common property 1
     */
    public final java.lang.String commonProperty1;
    /**
     * Property 1
     */
    public final java.lang.String property1;
    /**
     * Property 2
     */
    public final com.example.ItemProperty2 property2;
    /**
     * Decimal property
     */
    public final java.math.BigDecimal decimalProperty;
    /**
     * Local date time property
     */
    public final java.time.LocalDateTime localDateTimeProperty;
    /**
     * String array property
     */
    public final java.util.List<java.lang.String> stringArrayProperty;
    /**
     * Map property
     */
    public final java.util.Map<java.lang.String, java.math.BigDecimal> mapProperty;

    public Item(
            java.lang.String commonProperty1,
            java.lang.String property1,
            com.example.ItemProperty2 property2,
            java.math.BigDecimal decimalProperty,
            java.time.LocalDateTime localDateTimeProperty,
            java.util.List<java.lang.String> stringArrayProperty,
            java.util.Map<java.lang.String, java.math.BigDecimal> mapProperty
    ) {
        this.commonProperty1 = commonProperty1;
        this.property1 = property1;
        this.property2 = property2;
        this.decimalProperty = decimalProperty;
        this.localDateTimeProperty = localDateTimeProperty;
        this.stringArrayProperty = stringArrayProperty;
        this.mapProperty = mapProperty;
    }

    public static class Parser implements NonBlockingParser<com.example.Item> {

        private ObjectParserState objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
        private java.lang.String currentField;
        private java.lang.String p0; // commonProperty1
        private java.lang.String p1; // property1
        private com.example.ItemProperty2 p2; // property2
        private java.math.BigDecimal p3; // decimalProperty
        private java.time.LocalDateTime p4; // localDateTimeProperty
        private java.util.List<java.lang.String> p5; // stringArrayProperty
        private java.util.Map<java.lang.String, java.math.BigDecimal> p6; // mapProperty
        private final io.github.fomin.oasgen.NonBlockingParser<java.math.BigDecimal> parser0 = io.github.fomin.oasgen.ScalarParser.createNumberParser();
        private final io.github.fomin.oasgen.NonBlockingParser<java.time.LocalDateTime> parser1 = io.github.fomin.oasgen.ScalarParser.createStringLocalDateTimeParser();
        private final io.github.fomin.oasgen.NonBlockingParser<java.lang.String> parser2 = io.github.fomin.oasgen.ScalarParser.createStringParser();
        private final io.github.fomin.oasgen.NonBlockingParser<com.example.ItemProperty2> parser3 = new com.example.ItemProperty2.Parser();
        private final io.github.fomin.oasgen.NonBlockingParser<java.util.List<java.lang.String>> parser4 = new io.github.fomin.oasgen.ArrayParser<>(io.github.fomin.oasgen.ScalarParser.createStringParser());
        private final io.github.fomin.oasgen.NonBlockingParser<java.util.Map<java.lang.String, java.math.BigDecimal>> parser5 = new io.github.fomin.oasgen.MapParser<>(io.github.fomin.oasgen.ScalarParser.createNumberParser());

        @Override
        public boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException {
            while (jsonParser.currentToken() == null || jsonParser.currentToken() != JsonToken.NOT_AVAILABLE) {
                JsonToken token;
                switch (objectParserState) {
                    case PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL:
                        if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                            switch (token) {
                                case START_OBJECT:
                                    this.p0 = null;
                                    this.p1 = null;
                                    this.p2 = null;
                                    this.p3 = null;
                                    this.p4 = null;
                                    this.p5 = null;
                                    this.p6 = null;
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                    break;
                                case END_ARRAY:
                                    objectParserState = ObjectParserState.FINISHED_ARRAY;
                                    return true;
                                case VALUE_NULL:
                                    objectParserState = ObjectParserState.FINISHED_NULL;
                                    return true;
                                default:
                                    throw new RuntimeException("Unexpected token " + token);
                            }
                        }
                        break;
                    case PARSE_FIELD_NAME_OR_END_OBJECT:
                        if ((token = jsonParser.nextToken()) != JsonToken.NOT_AVAILABLE) {
                            switch (token) {
                                case FIELD_NAME:
                                    currentField = jsonParser.getCurrentName();
                                    objectParserState = ObjectParserState.PARSE_FIELD_VALUE;
                                    break;
                                case END_OBJECT:
                                    objectParserState = ObjectParserState.FINISHED_VALUE;
                                    return true;
                                default:
                                    throw new RuntimeException("Unexpected token " + token);
                            }
                        }
                        break;
                    case PARSE_FIELD_VALUE:
                        switch (currentField) {
                            case "commonProperty1":
                                if (parser2.parseNext(jsonParser)) {
                                    ParseResult<java.lang.String> parseResult = parser2.build();
                                    this.p0 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "property1":
                                if (parser2.parseNext(jsonParser)) {
                                    ParseResult<java.lang.String> parseResult = parser2.build();
                                    this.p1 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "property2":
                                if (parser3.parseNext(jsonParser)) {
                                    ParseResult<com.example.ItemProperty2> parseResult = parser3.build();
                                    this.p2 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "decimalProperty":
                                if (parser0.parseNext(jsonParser)) {
                                    ParseResult<java.math.BigDecimal> parseResult = parser0.build();
                                    this.p3 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "localDateTimeProperty":
                                if (parser1.parseNext(jsonParser)) {
                                    ParseResult<java.time.LocalDateTime> parseResult = parser1.build();
                                    this.p4 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "stringArrayProperty":
                                if (parser4.parseNext(jsonParser)) {
                                    ParseResult<java.util.List<java.lang.String>> parseResult = parser4.build();
                                    this.p5 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            case "mapProperty":
                                if (parser5.parseNext(jsonParser)) {
                                    ParseResult<java.util.Map<java.lang.String, java.math.BigDecimal>> parseResult = parser5.build();
                                    this.p6 = parseResult.getValue();
                                    objectParserState = ObjectParserState.PARSE_FIELD_NAME_OR_END_OBJECT;
                                }
                                break;
                            default:
                                throw new UnsupportedOperationException("Unexpected field " + currentField);
                        }
                        break;
                    default:
                        throw new RuntimeException("unexpected state " + objectParserState);
                }
            }
            return false;
        }

        @Override
        public ParseResult<com.example.Item> build() {
            switch (objectParserState) {
                case FINISHED_VALUE:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return new ParseResult.Value<>(new com.example.Item(this.p0, this.p1, this.p2, this.p3, this.p4, this.p5, this.p6));
                case FINISHED_ARRAY:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return ParseResult.endArray();
                case FINISHED_NULL:
                    objectParserState = ObjectParserState.PARSE_START_OBJECT_OR_END_ARRAY_OR_NULL;
                    return ParseResult.nullValue();
                default:
                    throw new IllegalStateException("Parsing is not completed");
            }
        }

    }

    public static class Writer implements io.github.fomin.oasgen.Writer<com.example.Item> {
        public static final Writer INSTANCE = new Writer();
        private static final io.github.fomin.oasgen.Writer<com.example.ItemProperty2> WRITER_0 = com.example.ItemProperty2.Writer.INSTANCE;
        private static final io.github.fomin.oasgen.Writer<java.math.BigDecimal> WRITER_1 = io.github.fomin.oasgen.ScalarWriter.NUMBER_WRITER;
        private static final io.github.fomin.oasgen.Writer<java.time.LocalDateTime> WRITER_2 = io.github.fomin.oasgen.ScalarWriter.STRING_LOCAL_DATE_TIME_WRITER;
        private static final io.github.fomin.oasgen.Writer<java.lang.String> WRITER_3 = io.github.fomin.oasgen.ScalarWriter.STRING_WRITER;
        private static final io.github.fomin.oasgen.Writer<java.util.List<java.lang.String>> WRITER_4 = new io.github.fomin.oasgen.ArrayWriter<>(io.github.fomin.oasgen.ScalarWriter.STRING_WRITER);
        private static final io.github.fomin.oasgen.Writer<java.util.Map<java.lang.String, java.math.BigDecimal>> WRITER_5 = new io.github.fomin.oasgen.MapWriter<>(io.github.fomin.oasgen.ScalarWriter.NUMBER_WRITER);

        @Override
        public void write(JsonGenerator jsonGenerator, com.example.Item value) throws IOException {
            jsonGenerator.writeStartObject();
            if (value.commonProperty1 != null) {
                jsonGenerator.writeFieldName("commonProperty1");
                WRITER_3.write(jsonGenerator, value.commonProperty1);
            }
            if (value.property1 != null) {
                jsonGenerator.writeFieldName("property1");
                WRITER_3.write(jsonGenerator, value.property1);
            }
            if (value.property2 != null) {
                jsonGenerator.writeFieldName("property2");
                WRITER_0.write(jsonGenerator, value.property2);
            }
            if (value.decimalProperty != null) {
                jsonGenerator.writeFieldName("decimalProperty");
                WRITER_1.write(jsonGenerator, value.decimalProperty);
            }
            if (value.localDateTimeProperty != null) {
                jsonGenerator.writeFieldName("localDateTimeProperty");
                WRITER_2.write(jsonGenerator, value.localDateTimeProperty);
            }
            if (value.stringArrayProperty != null) {
                jsonGenerator.writeFieldName("stringArrayProperty");
                WRITER_4.write(jsonGenerator, value.stringArrayProperty);
            }
            if (value.mapProperty != null) {
                jsonGenerator.writeFieldName("mapProperty");
                WRITER_5.write(jsonGenerator, value.mapProperty);
            }
            jsonGenerator.writeEndObject();
        }
    }
}
