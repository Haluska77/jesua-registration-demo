package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {

    private String newPassword;
    private String confirmNewPassword;
    private String token;

}