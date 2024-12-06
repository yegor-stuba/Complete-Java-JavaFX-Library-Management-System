package com.studyshare.server.exception;

import lombok.Getter;
import org.springframework.validation.ObjectError;
import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ObjectError> errors;

    public ValidationException(List<ObjectError> errors) {
        super("Validation failed");
        this.errors = errors;
    }
}