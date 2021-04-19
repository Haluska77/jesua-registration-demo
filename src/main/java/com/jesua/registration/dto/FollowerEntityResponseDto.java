package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class FollowerEntityResponseDto {

    private String email;
    private String name;
    private CourseResponseDto course;
    private Instant created;
    private Instant unregistered;
    private boolean accepted;
    private boolean gdpr;
    private String deviceDetail;
}
