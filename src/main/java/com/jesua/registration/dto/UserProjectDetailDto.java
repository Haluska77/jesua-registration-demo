package com.jesua.registration.dto;

import com.jesua.registration.entity.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class UserProjectDetailDto {

    private UserResponseBaseDto user;
    private ProjectRole role;
    private Instant created;

}
