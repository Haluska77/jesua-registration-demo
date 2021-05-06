package com.jesua.registration.service;

import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserProjectServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserProjectRepository userProjectRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserProjectService userProjectService;

    @Test
    void mapUserToProjectTest() {

        Project project = buildProject(1);

        User user = buildUserWithId(USER_ID);
        UserProject userProject = buildUserProject(project, user);
        String userProjectResponseDto = "User has been successfully mapped to project";

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(project)).when(projectRepository).findById(project.getId());
        doReturn(userProject).when(userProjectRepository).save(any());

        //run code
        String projectResponseDto = userProjectService.mapUserToProject(user.getId(), project.getId());

        //test
        verify(userRepository).findById(user.getId());
        verify(projectRepository).findById(project.getId());
        verify(userProjectRepository).save(any());

        assertThat(projectResponseDto).isNotNull();
        assertThat(projectResponseDto).isEqualTo(userProjectResponseDto);
    }
}