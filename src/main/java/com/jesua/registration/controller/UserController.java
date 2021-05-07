package com.jesua.registration.controller;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.security.dto.LoginDto;
import com.jesua.registration.security.dto.LoginResponseDto;
import com.jesua.registration.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("users/")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseBaseDto> users() {
        return userService.getAllUsers();
    }

    @PostMapping("update/{id}")
    public SuccessResponse<UserResponseBaseDto> update(@RequestBody UserDto userDto, @PathVariable UUID id) {
        UserResponseBaseDto userResponseDto = userService.updateUser(id, userDto);

        return new SuccessResponse<>(userResponseDto, "User has been successfully changed!");
    }

    @GetMapping("makeActive")
    @PreAuthorize("hasRole('ADMIN')")
    public SuccessResponse<UserResponseBaseDto> switchActiveUserAccount(
            @RequestParam("userId") UUID userId) {
        UserResponseBaseDto user = userService.switchActiveUserAccount(userId);

        return new SuccessResponse<>(user, "User has been changed");
    }

    @PostMapping("signin")
    public LoginResponseDto signIn(@RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        return userService.signIn(authentication);
    }

    @PostMapping("signup")
    @PreAuthorize("hasRole('ADMIN')")
    public SuccessResponse<UserResponseBaseDto> signUpUser(@RequestBody UserDto userDto) {

        UserResponseBaseDto userResponseDto = userService.createUser(userDto);
        return new SuccessResponse<>(userResponseDto, "New user registered successfully!");
    }
}