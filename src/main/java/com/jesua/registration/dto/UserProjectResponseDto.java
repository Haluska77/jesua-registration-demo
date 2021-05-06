package com.jesua.registration.dto;

import com.jesua.registration.entity.ProjectRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserProjectResponseDto {

    private ProjectResponseDto project;
    private UserResponseBaseDto user;
    private ProjectRole role;
    private Instant created;
}
