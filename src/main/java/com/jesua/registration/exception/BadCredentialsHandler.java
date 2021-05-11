package com.jesua.registration.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public interface BadCredentialsHandler {

    @ExceptionHandler
    default ResponseEntity<ErrorResponse<BadCredentialsException>> handleBadCredentials(BadCredentialsException exception) {

        ErrorResponse<BadCredentialsException> errorResponse = new ErrorResponse<>(null, "Meno alebo heslo sú neplatné!!!");

        return new ResponseEntity<>(errorResponse, FORBIDDEN);
    }
}
