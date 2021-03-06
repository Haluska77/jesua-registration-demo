package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CourseDto {

    private String description;
    private String startDate;
    private boolean open;
    private Integer capacity;
    private UUID userId;
    private String image;
    private long projectId;
}
