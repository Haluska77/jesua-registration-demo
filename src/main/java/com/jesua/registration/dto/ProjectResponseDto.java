package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ProjectResponseDto {

    private int id;
    private String shortName;
    private String description;
    private Instant created;
    private boolean active;
}
