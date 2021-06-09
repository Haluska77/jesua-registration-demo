package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PosterResponseDto {

    private long id;
    private String fileName;
    private String contentId;
    private ProjectResponseDto project;
    private Instant created;
}
