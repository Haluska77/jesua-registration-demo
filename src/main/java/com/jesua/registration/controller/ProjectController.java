package com.jesua.registration.controller;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.filter.ProjectFilter;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.ProjectService;
import com.jesua.registration.service.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects/")
public class ProjectController {

    private final ProjectService projectService;
    private final UserProjectService userProjectService;

    @GetMapping("")
    public List<ProjectResponseDto> all(ProjectFilter projectFilter) {
        return projectService.getProjects(projectFilter);
    }

    @PostMapping("add/{userId}")
    public UserProjectResponseDto add(@RequestBody ProjectDto projectDto, @PathVariable("userId") UUID userId) {
        return projectService.addProject(projectDto, userId);
    }

    @PostMapping("update/{id}")
    public ProjectResponseDto update(@RequestBody ProjectDto projectDto, @PathVariable("id") int id) {
        return projectService.updateProject(projectDto, id);
    }

    @PostMapping("map/user/{userId}/project/{projectId}")
    public SuccessResponse<String> mapParentToChild(@PathVariable UUID userId, @PathVariable long projectId) {

        String message = userProjectService.mapUserToProject(userId, projectId);

        return new SuccessResponse<>(null, message);
    }
}