package com.jesua.registration.exception;

public class PasswordTokenException extends RuntimeException {

    public PasswordTokenException(String message) {
        super(message);
    }

    public PasswordTokenException(Throwable cause, String message) {
        super(message, cause);
    }
}
