package com.jesua.registration.service;

import com.jesua.registration.dto.ProjectDetailDto;
import com.jesua.registration.dto.UserProjectIdResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.mapper.UserProjectMapper;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static com.jesua.registration.builder.UserProjectBuilder.buildProjectsDetailList;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProjectResponseDto;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProjectResponseIdDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    UserProjectMapper userProjectMapper;

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

    @Test
    void getAllUserProjectsDetailTest() {

        Project project = buildProject(1);

        User user = buildUserWithId(USER_ID);
        UserProject userProject = buildUserProject(project, user);

        List<UserProject> userProjectList = List.of(userProject);
        UserProjectResponseDto userProjectResponseDto = buildUserProjectResponseDto(userProject);

        doReturn(userProjectList).when(userProjectRepository).findAll();
        doReturn(userProjectResponseDto).when(userProjectMapper).mapEntityToDto(userProject);

        List<ProjectDetailDto> projectDetailDtos = buildProjectsDetailList(List.of(userProjectResponseDto));

        List<ProjectDetailDto> allUserProjectsDetail = userProjectService.getAllUserProjectsDetail();

        verify(userProjectRepository).findAll();
        verify(userProjectMapper).mapEntityToDto(userProject);

        assertThat(allUserProjectsDetail).usingRecursiveComparison().isEqualTo(projectDetailDtos);
    }

    @Test
    void getUserProjectDetailByProjectTest() {

        Project project = buildProject(1);

        User user = buildUserWithId(USER_ID);
        UserProject userProject = buildUserProject(project, user);

        List<UserProject> userProjectList = List.of(userProject);
        UserProjectResponseDto userProjectResponseDto = buildUserProjectResponseDto(userProject);

        doReturn(userProjectList).when(userProjectRepository).findByProjectId(project.getId());
        doReturn(userProjectResponseDto).when(userProjectMapper).mapEntityToDto(userProject);

        ProjectDetailDto projectDetailDto = buildProjectsDetailList(List.of(userProjectResponseDto)).get(0);

        ProjectDetailDto userProjectDetailByProject = userProjectService.getUserProjectDetailByProject(project.getId());

        verify(userProjectRepository).findByProjectId(project.getId());
        verify(userProjectMapper).mapEntityToDto(userProject);

        assertThat(userProjectDetailByProject).usingRecursiveComparison().isEqualTo(projectDetailDto);
    }

    @Test
    void getEmptyUserProjectListTest() {

        Project project = buildProject(1);

        User user = buildUserWithId(USER_ID);
        UserProject userProject = buildUserProject(project, user);

        doReturn(Collections.emptyList()).when(userProjectRepository).findByProjectId(project.getId());

        assertThatThrownBy(() -> userProjectService.getUserProjectDetailByProject(project.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Projekt neexistuje!");
    }

    @Test
    void getUserProjectIdListTest() {

        Project project = buildProject(1);

        User user = buildUserWithId(USER_ID);
        UserProject userProject = buildUserProject(project, user);

        List<UserProject> userProjectList = List.of(userProject);
        UserProjectIdResponseDto userProjectIdResponseDto = buildUserProjectResponseIdDto(userProject);

        doReturn(userProjectList).when(userProjectRepository).findAll();
        doReturn(userProjectIdResponseDto).when(userProjectMapper).mapEntityToDtoIds(userProject);

        List<UserProjectIdResponseDto> userProjectIdList = userProjectService.getUserProjectIdList();

        verify(userProjectRepository).findAll();
        verify(userProjectMapper).mapEntityToDtoIds(userProject);

        assertThat(userProjectIdList).usingRecursiveComparison().isEqualTo(List.of(userProjectIdResponseDto));

    }
}