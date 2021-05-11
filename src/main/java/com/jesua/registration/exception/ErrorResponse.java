package com.jesua.registration.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ErrorResponse<T> {

    private ErrorDto<T> error;

    public ErrorResponse(T object, String message) {
        error = new ErrorDto<T>(object, message);
    }

}
