package io.github.fomin.oasgen;

public abstract class ParseResult<T> {

    public static final ParseResult<?> END_ARRAY = new ParseResult<Object>() {
        @Override
        public Object getValue() {
            throw new UnsupportedOperationException();
        }
    };
    public static final ParseResult<?> NULL_VALUE = new ParseResult<Object>() {
        @Override
        public Object getValue() {
            return null;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> ParseResult<T> endArray() {
        return (ParseResult<T>) END_ARRAY;
    }

    @SuppressWarnings("unchecked")
    public static <T> ParseResult<T> nullValue() {
        return (ParseResult<T>) NULL_VALUE;
    }

    public static class Value<T> extends ParseResult<T> {
        public final T value;

        public Value(T value) {
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    private ParseResult() {
    }

    public abstract T getValue();

}
