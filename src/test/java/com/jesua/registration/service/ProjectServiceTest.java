package com.jesua.registration.service;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.mapper.ProjectMapper;
import com.jesua.registration.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ProjectMapper projectMapper;

    @InjectMocks
    ProjectService projectService;

    @Test
    void getProjectsTest() {
        Project project = buildProject(1);
        List<Project> projects = List.of(project);
        ProjectResponseDto projectResponseDto = buildProjectResponseDtoFromEntity(project);

        doReturn(projects).when(projectRepository).findAll();
        doReturn(projectResponseDto).when(projectMapper).mapEntityToDto(projects.get(0));

        List<ProjectResponseDto> actualResponseDto = projectService.getProjects();

        verify(projectRepository).findAll();
        verify(projectMapper).mapEntityToDto(projects.get(0));

        assertThat(actualResponseDto.get(0)).isNotNull();
        assertThat(actualResponseDto.get(0)).usingRecursiveComparison().isEqualTo(projectResponseDto);
    }

    @Test
    void addProjectTest() {

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProject(1);
        ProjectResponseDto expectedResponseDto = buildProjectResponseDtoFromEntity(project);

        doReturn(project).when(projectMapper).mapDtoToEntity(projectDto);
        doReturn(project).when(projectRepository).save(any());
        doReturn(expectedResponseDto).when(projectMapper).mapEntityToDto(project);

        //run code
        ProjectResponseDto projectResponseDto = projectService.addProject(projectDto);

        //test
        verify(projectMapper).mapDtoToEntity(projectDto);
        verify(projectRepository).save(any());
        verify(projectMapper).mapEntityToDto(project);

        assertThat(projectResponseDto).isNotNull();
        assertThat(projectResponseDto).usingRecursiveComparison().isEqualTo(expectedResponseDto);
    }

    @Test
    void updateProjectTest() {
        ProjectDto projectDto = buildProjectDto();
        Project origProject = buildProject(1);
        Project updatedProject = buildProjectFromDto(origProject, projectDto);
        ProjectResponseDto expectedResponseDto = buildProjectResponseDtoFromEntity(origProject);

        doReturn(origProject).when(projectRepository).getOne(1);
        doReturn(updatedProject).when(projectMapper).mapDtoToEntity(projectDto, origProject);
        doReturn(updatedProject).when(projectRepository).save(any());
        doReturn(expectedResponseDto).when(projectMapper).mapEntityToDto(updatedProject);

        //run code
        ProjectResponseDto projectResponseDto = projectService.updateProject(projectDto, 1);

        //test
        verify(projectRepository).getOne(1);
        verify(projectMapper).mapDtoToEntity(projectDto, origProject);
        verify(projectRepository).save(any());
        verify(projectMapper).mapEntityToDto(updatedProject);

        assertThat(projectResponseDto).isNotNull();
        assertThat(projectResponseDto).usingRecursiveComparison().isEqualTo(expectedResponseDto);
    }
}