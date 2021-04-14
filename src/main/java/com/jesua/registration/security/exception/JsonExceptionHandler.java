package com.jesua.registration.security.exception;

import com.jesua.registration.exception.ErrorResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class JsonExceptionHandler {

    public ResponseEntity<ErrorResponse<Throwable>> handleSignatureToken(JwtException exception) {
            ErrorResponse<Throwable> errorResponse = new ErrorResponse<>(exception.getCause(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), UNAUTHORIZED);
    }
}