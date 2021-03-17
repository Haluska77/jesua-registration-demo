package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseResponseDto {

    private int id;
    private String description;
    private String startDate;
    private boolean open;
    private int capacity;
    private String created;
    private UserResponseDto createdBy;
}
