package com.jesua.registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public interface MethodArgumentTypeMismatchHandler {

    @ExceptionHandler
    default ResponseEntity<ErrorResponse<MethodArgumentTypeMismatchException>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {

        Throwable cause = exception.getCause();

        ErrorResponse<MethodArgumentTypeMismatchException> errorResponse = new ErrorResponse<>(null, null);

        if (cause instanceof NumberFormatException) {
            errorResponse.setError(new ErrorDto<>(null, "Nepodarilo sa konvertovať text na číslo"));
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
