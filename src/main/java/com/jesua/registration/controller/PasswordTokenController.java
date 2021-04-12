package com.jesua.registration.controller;

import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.dto.UserTokenDto;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.PasswordTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("password/")
public class PasswordTokenController {

    private final PasswordTokenService passwordTokenService;

    //Get user by email and save token for reset password
    @GetMapping("/userAccount/{email}")
    public SuccessResponse<UserResponseDto> userAccount(@PathVariable String email) {

        UserResponseDto userResponseDto = passwordTokenService.createAndSendTokenByUserEmail(email);

        return new SuccessResponse<>(userResponseDto, "Na uvedený email bol poslaný link na zmenu hesla!");
    }

    @GetMapping("validateToken/{token}")
    public UserTokenDto validateToken(@PathVariable String token) {

        return passwordTokenService.validatePasswordResetToken(token);
    }

    @PostMapping("changePassword")
    public SuccessResponse<UserResponseDto> changePassword(
            @RequestBody PasswordDto passwordDto) { // TODO add entry PasswordDto validation

        UserResponseDto response = passwordTokenService.changePassword(passwordDto);
        return new SuccessResponse<>(response, "Password has been successfully changed !!!");
    }
}
