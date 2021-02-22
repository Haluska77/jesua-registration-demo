package com.jesua.registration.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ErrorDTO<T> implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T body;
    private String message;

    public ErrorDTO(T body, String message) {
        this.body = body;
        this.message = message;
    }
}
