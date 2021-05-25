package io.github.fomin.oasgen;

import java.util.Collections;
import java.util.List;

public final class ValidationException extends RuntimeException {

    public final List<? extends ValidationError> validationErrors;

    public ValidationException(List<? extends ValidationError> validationErrors) {
        super(validationErrors.toString());
        this.validationErrors = validationErrors;
    }

    public ValidationException(ValidationError validationError) {
        this(Collections.singletonList(validationError));
    }
}
