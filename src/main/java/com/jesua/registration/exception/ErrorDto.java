package com.jesua.registration.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ErrorDto<T> implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T body;
    private String message;

    public ErrorDto(T body, String message) {
        this.body = body;
        this.message = message;
    }
}
