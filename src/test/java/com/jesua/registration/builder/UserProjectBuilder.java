package com.jesua.registration.builder;

import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.ProjectRole;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseBaseDtoFromEntity;

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
}
