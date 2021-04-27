package com.jesua.registration.service;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.mapper.ProjectMapper;
import com.jesua.registration.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectResponseDto> getProjects() {

        return projectRepository.findAll()
                .stream().map(projectMapper::mapEntityToDto).collect(Collectors.toList());
    }

    public ProjectResponseDto addProject(ProjectDto projectDto) {

        Project project = projectMapper.mapDtoToEntity(projectDto);
        projectRepository.save(project);

        return projectMapper.mapEntityToDto(project);
    }

    public ProjectResponseDto updateProject(ProjectDto projectDto, long id) {

        Project savedProject = projectRepository.getOne(id);
        Project project = projectMapper.mapDtoToEntity(projectDto, savedProject);
        projectRepository.save(project);

        return projectMapper.mapEntityToDto(project);
    }


    public Project getProject(Long id) {

        return projectRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
