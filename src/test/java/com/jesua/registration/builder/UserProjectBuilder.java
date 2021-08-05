package com.jesua.registration.builder;

import com.jesua.registration.dto.ProjectDetailDto;
import com.jesua.registration.dto.UserProjectDetailDto;
import com.jesua.registration.dto.UserProjectIdResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.ProjectRole;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;

import java.util.List;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseBaseDtoFromEntity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class UserProjectBuilder {

    public static UserProject buildUserProject(Project project, User user) {
        UserProject userProject = new UserProject();
        userProject.setProject(project);
        userProject.setUser(user);
        userProject.setRole(ProjectRole.OWNER);
        return userProject;
    }

    public static UserProjectResponseDto buildUserProjectResponseDto(UserProject userProject) {

        UserProjectResponseDto userProjectResponseDto = new UserProjectResponseDto();
        userProjectResponseDto.setProject(buildProjectResponseDtoFromEntity(userProject.getProject()));
        userProjectResponseDto.setUser(buildUserResponseBaseDtoFromEntity(userProject.getUser()));
        userProjectResponseDto.setRole(ProjectRole.OWNER);
        return userProjectResponseDto;
    }

    public static UserProjectIdResponseDto buildUserProjectResponseIdDto(UserProject userProject) {

        UserProjectIdResponseDto userProjectIdResponseDto = new UserProjectIdResponseDto();
        userProjectIdResponseDto.setProject(userProject.getProject().getId());
        userProjectIdResponseDto.setUser(userProject.getUser().getId());
        userProjectIdResponseDto.setRole(ProjectRole.OWNER);
        return userProjectIdResponseDto;
    }

    public static List<ProjectDetailDto> buildProjectsDetailList(List<UserProjectResponseDto> userProjectList) {
        return userProjectList.stream()
                .collect(groupingBy(p -> p.getProject().getId()))
                .values().stream()
                .map(userProjectResponseDtos -> new ProjectDetailDto(userProjectResponseDtos.get(0).getProject(), userProjectResponseDtos.stream()
                                .map(user ->
                                        new UserProjectDetailDto(user.getUser(), user.getRole(), user.getCreated())
                                ).collect(toList())
                        )
                ).collect(toList());
    }

    public static UserProjectIdResponseDto buildUserProjectId(UserProject userProject) {
        UserProjectIdResponseDto userProjectIdResponseDto = new UserProjectIdResponseDto();
        userProjectIdResponseDto.setProject(userProject.getProject().getId());
        userProjectIdResponseDto.setUser(userProject.getUser().getId());
        userProjectIdResponseDto.setRole(userProject.getRole());

        return userProjectIdResponseDto;
    }
}
