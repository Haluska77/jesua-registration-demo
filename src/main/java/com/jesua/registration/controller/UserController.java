package com.jesua.registration.controller;

import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.mapper.ProjectMapper;
import com.jesua.registration.security.dto.LoginDto;
import com.jesua.registration.security.dto.LoginResponseDto;
import com.jesua.registration.security.jwt.JwtProvider;
import com.jesua.registration.security.services.UserAuthPrincipal;
import com.jesua.registration.service.UserProjectService;
import com.jesua.registration.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@AllArgsConstructor
@RequestMapping("users/")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserProjectService userProjectService;
    private final JwtProvider jwtProvider;
    private final ProjectMapper projectMapper;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> users() {
        return userService.getAllUsers();
    }

    @PostMapping("update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuccessResponse<UserResponseDto> update(@RequestBody UserDto userDto, @PathVariable UUID id) {
        UserResponseDto userResponseDto = userService.updateUser(id, userDto);

        return new SuccessResponse<>(userResponseDto, "User has been successfully changed!");
    }

    @GetMapping("makeActive")
    @PreAuthorize("hasRole('ADMIN')")
    public SuccessResponse<UserResponseDto> switchActiveUserAccount(
            @RequestParam("userId") UUID userId) {
        UserResponseDto user = userService.switchActiveUserAccount(userId);

        return new SuccessResponse<>(user, "User has been changed");
    }

    @PostMapping("signin")
    public ResponseEntity<LoginResponseDto> signIn(@RequestBody LoginDto loginDto) {

        Authentication authentication;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateJwtToken(authentication);

        UserAuthPrincipal userDetails = (UserAuthPrincipal) authentication.getPrincipal();
        Set<ProjectResponseDto> projects = projectMapper.mapEntitySetToDtoSet(userDetails.getProjects());

        return ResponseEntity.ok(new LoginResponseDto(userDetails.getId(), userDetails.getAvatar(), userDetails.getName(),
                userDetails.getUsername(), userDetails.getRole(),
                userDetails.isEnabled(), userDetails.getCreated(), projects, jwtToken));

    }

    @PostMapping("signup")
    @PreAuthorize("hasRole('ADMIN')")
    public SuccessResponse<UserResponseDto> signUpUser(@RequestBody UserDto userDto) {

        UserResponseDto userResponseDto = userService.createUser(userDto);
        return new SuccessResponse<>(userResponseDto, "New user registered successfully!");
    }

    @PostMapping("map/user/{userId}/project/{projectId}")
    public SuccessResponse<String> mapParentToChild(@PathVariable UUID userId, @PathVariable long projectId) {

        String message = userProjectService.mapUserToProject(userId, projectId);

        return new SuccessResponse<>(null, message);
    }
}