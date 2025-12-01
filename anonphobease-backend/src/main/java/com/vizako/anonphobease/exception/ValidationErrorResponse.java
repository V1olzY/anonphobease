package com.vizako.anonphobease.exception;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse {

    private final List<ValidationError> errors = new ArrayList<>();

    public void addFieldError(FieldError fieldError) {
        ValidationError error = new ValidationError();
        error.setField(fieldError.getField());
        error.setMessage(fieldError.getDefaultMessage());
        errors.add(error);
    }

    public void addCustomError(String field, String message) {
        ValidationError error = new ValidationError();
        error.setField(field);
        error.setMessage(message);
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
