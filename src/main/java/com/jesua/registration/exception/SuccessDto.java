package com.jesua.registration.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SuccessDto<T> implements Serializable {
    private T body;
    private int length = 1;
    private String message;

    public SuccessDto(T body) {
        this.body = body;
        if (this.body instanceof List) {
            this.length = ((List) this.body).size();
        }
        if (this.body instanceof Map) {
            this.length = ((Map) this.body).size();
        }
    }

    public SuccessDto(T body, String message) {
        this.body = body;
        this.message = message;
        if (this.body instanceof List) {
            this.length = ((List) this.body).size();
        }
        if (this.body instanceof Map) {
            this.length = ((Map) this.body).size();
        }
    }
}
