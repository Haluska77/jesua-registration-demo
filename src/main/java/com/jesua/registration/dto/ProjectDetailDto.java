package com.jesua.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectDetailDto {

    private ProjectResponseDto project;
    private List<UserProjectDetailDto> users;
}
