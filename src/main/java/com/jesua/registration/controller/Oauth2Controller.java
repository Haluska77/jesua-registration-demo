package com.jesua.registration.controller;

import com.jesua.registration.security.dto.LoginResponseDto;
import com.jesua.registration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Oauth2Controller {

    private final UserService userService;

    @GetMapping("oauth2/user")
    public LoginResponseDto getUserName(Authentication authentication) {

        return userService.signIn(authentication);
    }

}
