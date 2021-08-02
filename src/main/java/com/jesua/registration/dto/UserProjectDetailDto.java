package com.jesua.registration.dto;

import com.jesua.registration.entity.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class UserProjectDetailDto {

    private UserResponseBaseDto user;
    private ProjectRole role;
    private Instant created;

}
