package com.jesua.registration.service;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.mapper.ProjectMapper;
import com.jesua.registration.mapper.UserProjectMapper;
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
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDtoAndSavedProject;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProjectResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserProjectRepository userProjectRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProjectMapper projectMapper;

    @Mock
    UserProjectMapper userProjectMapper;

    @InjectMocks
    ProjectService projectService;

    @Test
    void addProjectTest() {

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProject(1);

        User user = buildUserWithId(USER_ID);
        UserProject userProject = buildUserProject(project, user);
        UserProjectResponseDto userProjectResponseDto = buildUserProjectResponseDto(userProject);

        doReturn(project).when(projectMapper).mapDtoToEntity(projectDto);
        doReturn(project).when(projectRepository).save(any());
        doReturn(Optional.of(user)).when(userRepository).findById(USER_ID);
        doReturn(userProject).when(userProjectRepository).save(any());
        doReturn(userProjectResponseDto).when(userProjectMapper).mapEntityToDto(userProject);

        //run code
        UserProjectResponseDto projectResponseDto = projectService.addProject(projectDto, USER_ID);

        //test
        verify(projectMapper).mapDtoToEntity(projectDto);
        verify(projectRepository).save(any());
        verify(userRepository).findById(USER_ID);
        verify(userProjectRepository).save(any());
        verify(userProjectMapper).mapEntityToDto(userProject);

        assertThat(projectResponseDto).isNotNull();
        assertThat(projectResponseDto).usingRecursiveComparison().isEqualTo(userProjectResponseDto);
    }

    @Test
    void updateProjectTest() {
        ProjectDto projectDto = buildProjectDto();
        Project origProject = buildProject(1);
        Project updatedProject = buildProjectFromDtoAndSavedProject(origProject, projectDto);
        ProjectResponseDto expectedResponseDto = buildProjectResponseDtoFromEntity(origProject);

        doReturn(Optional.of(origProject)).when(projectRepository).findById(1L);
        doReturn(updatedProject).when(projectMapper).mapDtoToEntity(projectDto, origProject);
        doReturn(updatedProject).when(projectRepository).save(any());
        doReturn(expectedResponseDto).when(projectMapper).mapEntityToDto(updatedProject);

        //run code
        ProjectResponseDto projectResponseDto = projectService.updateProject(projectDto, 1);

        //test
        verify(projectRepository).findById(1L);
        verify(projectMapper).mapDtoToEntity(projectDto, origProject);
        verify(projectRepository).save(any());
        verify(projectMapper).mapEntityToDto(updatedProject);

        assertThat(projectResponseDto).isNotNull();
        assertThat(projectResponseDto).usingRecursiveComparison().isEqualTo(expectedResponseDto);
    }
}