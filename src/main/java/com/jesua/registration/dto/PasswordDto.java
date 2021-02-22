package com.jesua.registration.dto;

import lombok.Getter;

@Getter
public class PasswordDto {

    private String newPassword;
    private String confirmNewPassword;
    private String token;

}