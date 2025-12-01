package com.vizako.anonphobease.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(Exception ex) {
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();

        if (ex instanceof MethodArgumentNotValidException manve) {
            for (FieldError fieldError : manve.getBindingResult().getFieldErrors()) {
                validationErrors.addFieldError(fieldError);
            }
        } else if (ex instanceof BindException be) {
            for (FieldError fieldError : be.getBindingResult().getFieldErrors()) {
                validationErrors.addFieldError(fieldError);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
    }
}
