package com.jesua.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserTokenDto {
    private String token;
    private TokenState tokenState;
}
