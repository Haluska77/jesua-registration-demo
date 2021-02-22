package com.jesua.registration.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
//    private String userName;
    private String email;
    private String password;
    private List<String> role;
}
