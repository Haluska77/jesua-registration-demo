package com.jesua.registration.controller;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("projects/")
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public List<ProjectResponseDto> all() {
        return projectService.getProjects();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("add")
    public ProjectResponseDto add(@RequestBody ProjectDto projectDto) {
        return projectService.addProject(projectDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("update/{id}")
    public ProjectResponseDto update(@RequestBody ProjectDto projectDto, @PathVariable("id") int id) {
        return projectService.updateProject(projectDto, id);
    }

}