package com.jesua.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProjectDetailDto {

    private ProjectResponseDto project;
    private List<UserProjectDetailDto> users;
}
