package com.jesua.registration.controller;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.mapper.UserProjectMapper;
import com.jesua.registration.security.dto.LoginDto;
import com.jesua.registration.security.dto.LoginResponseDto;
import com.jesua.registration.security.jwt.JwtProvider;
import com.jesua.registration.security.services.UserAuthPrincipal;
import com.jesua.registration.service.UserProjectService;
import com.jesua.registration.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("users/")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserProjectService userProjectService;
    private final JwtProvider jwtProvider;
    private final UserProjectMapper userProjectMapper;

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

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateJwtToken(authentication);

        UserAuthPrincipal userDetails = (UserAuthPrincipal) authentication.getPrincipal();

        Set<UserProject> userProjects = userDetails.getProjects().stream().map(
                p -> {
                    UserProjectId userProjectId = new UserProjectId();
                    userProjectId.setUserId(userDetails.getId());
                    userProjectId.setProjectId(p.getId());
                    return userProjectId;
                }
        ).collect(Collectors.toSet())
                .stream().map(userProjectService::findById
                ).collect(Collectors.toSet());

        Set<UserProjectResponseDto> userProjectResponseDtoSet = userProjectMapper.mapEntitySetToDtoSet(userProjects);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setId(userDetails.getId());
        loginResponseDto.setName(userDetails.getName());
        loginResponseDto.setEmail(userDetails.getUsername());
        loginResponseDto.setRole(userDetails.getRole());
        loginResponseDto.setAvatar(userDetails.getAvatar());
        loginResponseDto.setActive(userDetails.isEnabled());
        loginResponseDto.setCreated(userDetails.getCreated());
        loginResponseDto.setProjects(userProjectResponseDtoSet);
        loginResponseDto.setToken(jwtToken);

        return loginResponseDto;

    }

    @PostMapping("signup")
    @PreAuthorize("hasRole('ADMIN')")
    public SuccessResponse<UserResponseBaseDto> signUpUser(@RequestBody UserDto userDto) {

        UserResponseBaseDto userResponseDto = userService.createUser(userDto);
        return new SuccessResponse<>(userResponseDto, "New user registered successfully!");
    }
}