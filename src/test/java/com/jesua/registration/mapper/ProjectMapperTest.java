package com.jesua.registration.mapper;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {

    @InjectMocks
    ProjectMapperImpl projectMapper;

    @Test
    void mapDtoToEntityTest() {

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);

        Project actualProject = projectMapper.mapDtoToEntity(projectDto);

        assertThat(actualProject).usingRecursiveComparison().isEqualTo(project);
    }

    @Test
    void mapDtoToExistingEntityTest() {

        Project savedProject = buildProject(1);
        ProjectDto projectDto = buildProjectDto();
        Project expectedProject = buildProjectFromDto(projectDto);
        expectedProject.setId(savedProject.getId());

        Project actualProject = projectMapper.mapDtoToEntity(projectDto, savedProject);

        assertThat(actualProject).usingRecursiveComparison().isEqualTo(expectedProject);

    }

    @Test
    void mapEntityToResponseDtoTest() {

        Project savedProject = buildProject(1);
        ProjectResponseDto expectedResponseDto = buildProjectResponseDtoFromEntity(savedProject);

        ProjectResponseDto actualResponseDto = projectMapper.mapEntityToDto(savedProject);

        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(expectedResponseDto);

    }
}