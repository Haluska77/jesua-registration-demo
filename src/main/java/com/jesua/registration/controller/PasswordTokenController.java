package com.jesua.registration.controller;

import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.dto.UserTokenDto;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.PasswordTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("password/")
public class PasswordTokenController {

    private final PasswordTokenService passwordTokenService;

    public static final String USER_NOT_FOUND_OR_NOT_ACTIVE = "User not Found or not active";

    //Get user by email and save token for reset password
    @GetMapping("/userAccount")
    public UserResponseDto userAccount(
            @RequestParam("email") String userEmail) {

        UserResponseDto userResponseDto = passwordTokenService.createAndSendTokenByUserEmail(userEmail);

        if (userResponseDto == null) {
            throw new NoSuchElementException(USER_NOT_FOUND_OR_NOT_ACTIVE);
        }

        return userResponseDto;
    }

    // submit token from email and send back to UI
    // if result = success, open change password form in UI, otherwise display blank page and error message
    @GetMapping("resetPassword")
    public UserTokenDto validateToken(
            @RequestParam("token") String token) {

        return passwordTokenService.validatePasswordResetToken(token);
    }

    @PostMapping("changePassword")
    public SuccessResponse<UserResponseDto> changePassword(
            @RequestBody PasswordDto passwordDto) { // TODO add entry PasswordDto validation

        UserResponseDto response = passwordTokenService.changePassword(passwordDto);
        return new SuccessResponse<>(response, "Password has been successfully changed !!!");
    }
}
