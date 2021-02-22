package com.jesua.registration.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtMalformedException extends AuthenticationException {

    public JwtMalformedException(String invalid_jwt_signature) {
        super(invalid_jwt_signature);

    }
}
