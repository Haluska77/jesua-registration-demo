package com.jesua.registration.security.dto;

import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserResponseDto;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
public class LoginResponseDto extends UserResponseDto {

    private String token;

    public LoginResponseDto(UUID id, String avatar, String name, String email, String role, Boolean active, Instant created, Set<ProjectResponseDto> projects, String token) {
        super(id, avatar, name, email, role, active, created, projects);
        this.token = token;
    }
}
