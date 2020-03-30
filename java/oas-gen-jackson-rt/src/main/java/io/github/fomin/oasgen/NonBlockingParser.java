package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import java.io.IOException;

public interface NonBlockingParser<T> {
     boolean parseNext(NonBlockingJsonParser jsonParser) throws IOException;
     ParseResult<T> build();
}
