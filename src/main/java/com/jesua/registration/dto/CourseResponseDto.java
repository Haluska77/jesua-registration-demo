package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CourseResponseDto {

    private long id;
    private String description;
    private String startDate;
    private boolean open;
    private int capacity;
    private Instant created;
    private UserResponseBaseDto createdBy;
    private String image;
    private ProjectResponseDto project;
}
