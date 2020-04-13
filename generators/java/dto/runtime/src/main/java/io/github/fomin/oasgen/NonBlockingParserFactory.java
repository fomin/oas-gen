package io.github.fomin.oasgen;

public interface NonBlockingParserFactory<T> {
    NonBlockingParser<T> create();
}
