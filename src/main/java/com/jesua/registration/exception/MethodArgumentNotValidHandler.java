package com.jesua.registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MethodArgumentNotValidHandler {

    @ExceptionHandler
    default ResponseEntity<ErrorResponse<MethodArgumentNotValidException>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {

        Stream<String> fieldViolations = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError ->
                        String.format("%s %s", fieldError.getField(), Objects.requireNonNull(fieldError.getDefaultMessage()))
                );

        Stream<String> globalViolations = exception.getBindingResult().getGlobalErrors().stream()
                .map(globalError ->
                        String.format("%s %s", globalError.getObjectName(), Objects.requireNonNull(globalError.getDefaultMessage()))
                );

        String violations = Stream.concat(fieldViolations, globalViolations)
                .collect(Collectors.joining(", "));

        ErrorResponse<MethodArgumentNotValidException> errorResponse = new ErrorResponse<>(null, violations);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
