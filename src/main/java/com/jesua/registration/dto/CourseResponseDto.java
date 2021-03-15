package com.jesua.registration.dto;

import com.jesua.registration.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CourseResponseDto {

    private int id;
    private String description;
    private String startDate;
    private boolean open;
    private int capacity;
    private String created;
    private User user;
}
