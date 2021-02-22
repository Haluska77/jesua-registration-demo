package com.jesua.registration.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse<T> {
    private SuccessDTO<T> response;

    public SuccessResponse(T object) {
        this.response = new SuccessDTO<>(object);
    }

    public SuccessResponse(T object, String message) {
        this.response = new SuccessDTO<>(object, message);
    }
}
