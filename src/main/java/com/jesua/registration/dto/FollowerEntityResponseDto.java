package com.jesua.registration.dto;

import com.jesua.registration.entity.Course;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
public class FollowerEntityResponseDto {

    private String email;
    private String name;
    private Course course;
    private Instant registered;
    private Instant unregistered;
    private boolean accepted;
    private boolean gdpr;

}
