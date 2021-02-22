package com.jesua.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenState {
    SUCCESS("Token is valid !!!"),
    EXPIRED("Token has expired !!!"),
    INVALID("Token is invalid !!!"),
    APPLIED("Token is applied, create new request !!!");

    private String message;
}
