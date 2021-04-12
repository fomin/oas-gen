package io.github.fomin.oasgen;

// TODO REMOVE
public enum ArrayParserState {
    PARSE_START_ARRAY_OR_NULL_VALUE_OR_END_ARRAY,
    PARSE_VALUE,
    FINISHED_ARRAY,
    FINISHED_OUTER_ARRAY,
    FINISHED_NULL
}
