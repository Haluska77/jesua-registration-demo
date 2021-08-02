package com.jesua.registration.dto;

import com.jesua.registration.entity.ProjectRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserProjectIdResponseDto {

    private long project;
    private UUID user;
    private ProjectRole role;
    private Instant created;
}
