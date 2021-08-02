package com.jesua.registration.controller;

import com.jesua.registration.dto.ProjectDetailDto;
import com.jesua.registration.dto.UserProjectIdResponseDto;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("userProjects/")
public class UserProjectController {

    private final UserProjectService userProjectService;

    @GetMapping("")
    public List<ProjectDetailDto> getUserProjects() {
        return userProjectService.getAllUserProjectsDetail();
    }

    @GetMapping("project/{id}")
    public ProjectDetailDto getUserProjectsByProject(@PathVariable("id") long projectId) {
        return userProjectService.getUserProjectDetailByProject(projectId);
    }

    @GetMapping("list")
    public List<UserProjectIdResponseDto> getUserProjectIdList() {
        return userProjectService.getUserProjectIdList();
    }

    @PostMapping("map/user/{userId}/project/{projectId}")
    public SuccessResponse<String> mapParentToChild(@PathVariable UUID userId, @PathVariable long projectId) {

        String message = userProjectService.mapUserToProject(userId, projectId);

        return new SuccessResponse<>(null, message);
    }
}