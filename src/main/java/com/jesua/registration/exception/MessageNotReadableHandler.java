package com.jesua.registration.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public interface MessageNotReadableHandler {

    @ExceptionHandler
    default ResponseEntity<ErrorResponse<HttpMessageNotReadableException>> handleMethodArgumentNotValid(HttpMessageNotReadableException exception) {

        Throwable cause = exception.getCause();

        ErrorResponse<HttpMessageNotReadableException> errorResponse = new ErrorResponse<>(null, null);

        if (cause instanceof JsonParseException) {
            errorResponse.setError(new ErrorDto<>(null, "Parsing JSON Error"));
        }

        if (cause instanceof InvalidFormatException) {
            errorResponse.setError(new ErrorDto<>(null, "Invalid Format Error"));
        }

        if (cause instanceof MismatchedInputException) {
            errorResponse.setError(new ErrorDto<>(null, "Not valid input type has been inserted"));
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
