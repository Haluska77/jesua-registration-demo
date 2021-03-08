package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDto {

    private UUID id;
    private String name;
    private String email;
    private String role;
    private Boolean active;
    private Instant created;
}
