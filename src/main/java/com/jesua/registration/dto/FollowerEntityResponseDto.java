package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class FollowerEntityResponseDto {

    private UUID id;
    private String email;
    private String name;
    private String token;
    private CourseResponseDto course;
    private Instant created;
    private Instant unregistered;
    private boolean accepted;
    private boolean gdpr;
    private String deviceDetail;
}
