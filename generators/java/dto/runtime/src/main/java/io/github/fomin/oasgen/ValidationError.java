package io.github.fomin.oasgen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ValidationError {

    public static class NodeTypeError extends ValidationError {

        @Nonnull
        public final JsonNodeType expectedType;
        @Nonnull
        public final JsonNodeType actualType;

        public NodeTypeError(
                @Nonnull JsonNodeType expectedType,
                @Nonnull JsonNode actualNode
        ) {
            this.expectedType = expectedType;
            this.actualType = actualNode.getNodeType();
        }

        @Override
        public String toString() {
            return "Expected node of type " + expectedType + " but got " + actualType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeTypeError that = (NodeTypeError) o;
            return expectedType == that.expectedType && actualType == that.actualType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(expectedType, actualType);
        }
    }

    public static class ArrayItemError extends ValidationError {

        public final int index;
        @Nonnull
        public final List<? extends ValidationError> itemErrors;

        public ArrayItemError(
                int index,
                @Nonnull List<? extends ValidationError> itemErrors
        ) {
            this.index = index;
            this.itemErrors = itemErrors;
        }

        @Override
        public String toString() {
            return "Got errors at index " + index + ", errors = " + itemErrors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayItemError that = (ArrayItemError) o;
            return index == that.index && itemErrors.equals(that.itemErrors);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, itemErrors);
        }
    }

    public static class ObjectFieldError extends ValidationError {

        @Nonnull
        public final String field;
        @Nonnull
        public final List<? extends ValidationError> fieldErrors;

        public ObjectFieldError(
                @Nonnull String field,
                @Nonnull List<? extends ValidationError> fieldErrors
        ) {
            this.field = field;
            this.fieldErrors = fieldErrors;
        }

        @Override
        public String toString() {
            return "Field \"" + field + "\" has errors = " + fieldErrors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ObjectFieldError that = (ObjectFieldError) o;
            return field.equals(that.field) && fieldErrors.equals(that.fieldErrors);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, fieldErrors);
        }
    }

    public static class ValueError extends ValidationError {

        @Nonnull
        public final String code;
        @Nonnull
        public final JsonNode value;
        @Nonnull
        public final Map<String, String> params;

        public ValueError(
                @Nonnull String code,
                @Nonnull JsonNode value,
                @Nonnull Map<String, String> params
        ) {
            this.code = code;
            this.value = value;
            this.params = params;
        }

        public ValueError(String code, JsonNode value) {
            this(code, value, Collections.emptyMap());
        }

        @Override
        public String toString() {
            return "Error " + code + ". Params = " + params + ". Value = " + value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValueError that = (ValueError) o;
            return code.equals(that.code) && value.equals(that.value) && params.equals(that.params);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, value, params);
        }
    }

    public static class StringValue extends ValidationError {

        @Nonnull
        public final String code;
        @Nonnull
        public final String value;
        @Nonnull
        public final Map<String, String> params;

        public StringValue(
                @Nonnull String code,
                @Nonnull String value,
                @Nonnull Map<String, String> params
        ) {
            this.code = code;
            this.value = value;
            this.params = params;
        }

        public StringValue(String code, String value) {
            this(code, value, Collections.emptyMap());
        }

        @Override
        public String toString() {
            return "Error " + code + ". Params = " + params + ". Value = " + value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringValue that = (StringValue) o;
            return code.equals(that.code) && value.equals(that.value) && params.equals(that.params);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, value, params);
        }
    }
}
