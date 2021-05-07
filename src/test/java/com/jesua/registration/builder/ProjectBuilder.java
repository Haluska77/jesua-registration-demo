package com.jesua.registration.builder;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.filter.ProjectFilter;
import com.jesua.registration.repository.ProjectSpecification;

import java.util.Set;
import java.util.stream.Collectors;

public class ProjectBuilder {

    public static Project buildProject(long id){

        Project project = new Project();
        project.setId(id);
        project.setShortName("orig jesua");
        project.setDescription("orig description");
        project.setActive(true);

        return project;
    }

    public static Project buildProjectFromDtoAndSavedProject(Project origProject, ProjectDto projectDto){

        Project project = new Project();
        project.setId(origProject.getId());
        project.setShortName(projectDto.getShortName());
        project.setDescription(projectDto.getDescription());
        project.setCreated(origProject.getCreated());
        project.setActive(projectDto.isActive());

        return project;
    }

    public static ProjectDto buildProjectDto(){

        ProjectDto projectDto = new ProjectDto();
        projectDto.setShortName("jesua");
        projectDto.setDescription("new description");
        projectDto.setActive(true);

        return projectDto;
    }

    public static Project buildProjectFromDto(ProjectDto projectDto){

        Project project = new Project();
        project.setShortName(projectDto.getShortName());
        project.setDescription(projectDto.getDescription());
        project.setActive(projectDto.isActive());

        return project;
    }

    // output project submitted to UI
    public static ProjectResponseDto buildProjectResponseDtoFromEntity(Project project){

        ProjectResponseDto projectResponseDto = new ProjectResponseDto();
        projectResponseDto.setId(project.getId());
        projectResponseDto.setShortName(project.getShortName());
        projectResponseDto.setDescription(project.getDescription());
        projectResponseDto.setCreated(project.getCreated());
        projectResponseDto.setActive(project.isActive());

        return projectResponseDto;
    }

    public static Set<ProjectResponseDto> buildProjectResponseDtoSetFromEntitySet(Set<Project> project) {

        return project.stream().map(p -> {
                    ProjectResponseDto projectResponseDto = new ProjectResponseDto();
                    projectResponseDto.setId(p.getId());
                    projectResponseDto.setShortName(p.getShortName());
                    projectResponseDto.setDescription(p.getDescription());
                    projectResponseDto.setCreated(p.getCreated());
                    projectResponseDto.setActive(p.isActive());
                    return projectResponseDto;
                }
        )
                .collect(Collectors.toSet());
    }

    public static ProjectSpecification createProjectSpecification(String name){
        ProjectFilter projectFilter = ProjectFilter.builder().name(name).build();
        return new ProjectSpecification(projectFilter);
    }

}
