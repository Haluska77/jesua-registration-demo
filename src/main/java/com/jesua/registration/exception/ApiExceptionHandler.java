package com.jesua.registration.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    public static final String NAME_OR_PWD_NOT_VALID = "Meno alebo heslo sú neplatné!!!";

    @ExceptionHandler()
    public ResponseEntity<ErrorResponse<Throwable>> exceptionHandlerResponse(Exception exception) {

        ErrorResponse<Throwable> errorResponse = new ErrorResponse<>(exception.getCause(), exception.getMessage());
        HttpStatus status = INTERNAL_SERVER_ERROR;
        if (exception instanceof PasswordTokenException || exception instanceof NoSuchElementException) {
            status = NOT_FOUND;
        }

        if (exception instanceof BadCredentialsException) {
            status = FORBIDDEN;
            errorResponse.setError(new ErrorDTO<>(null, NAME_OR_PWD_NOT_VALID));
        }

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), status);
    }
}