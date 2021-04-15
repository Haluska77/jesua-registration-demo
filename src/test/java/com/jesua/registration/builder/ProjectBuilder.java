package com.jesua.registration.builder;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;

import java.time.Instant;

public class ProjectBuilder {

    public static Project buildProject(int id){

        Project project = new Project();
        project.setId(id);
        project.setShortName("orig jesua");
        project.setDescription("orig description");
        project.setCreated(Instant.now());

        return project;
    }

    public static Project buildProject(Project origProject, ProjectDto projectDto){

        Project project = new Project();
        project.setId(origProject.getId());
        project.setShortName(projectDto.getShortName());
        project.setDescription(projectDto.getDescription());
        project.setCreated(origProject.getCreated());

        return project;
    }
    public static ProjectDto buildProjectDto(){

        ProjectDto projectDto = new ProjectDto();
        projectDto.setShortName("jesua");
        projectDto.setDescription("new description");

        return projectDto;
    }

    public static Project buildProjectFromDto(ProjectDto projectDto){

        Project project = new Project();
        project.setShortName(projectDto.getShortName());
        project.setDescription(projectDto.getDescription());
        project.setCreated(Instant.now());

        return project;
    }

    // output project submitted to UI
    public static ProjectResponseDto buildProjectResponseDtoFromEntity(Project project){

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setId(project.getId());
        projectResponseDto.setShortName(project.getShortName());
        projectResponseDto.setDescription(project.getDescription());
        projectResponseDto.setCreated(project.getCreated());

        return projectResponseDto;
    }
}