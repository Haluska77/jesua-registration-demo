package com.jesua.registration.service;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.ProjectRole;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.filter.ProjectFilter;
import com.jesua.registration.mapper.ProjectMapper;
import com.jesua.registration.mapper.UserProjectMapper;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.ProjectSpecification;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectMapper projectMapper;
    private final UserProjectMapper userProjectMapper;
    private final UserRepository userRepository;

    public List<ProjectResponseDto> getProjects(ProjectFilter projectFilter) {
        Specification<Project> projectSpecification = new ProjectSpecification(projectFilter);
        return projectRepository.findAll(projectSpecification)
                .stream().map(projectMapper::mapEntityToDto).collect(Collectors.toList());
    }

    @Transactional
    public UserProjectResponseDto addProject(ProjectDto projectDto, UUID userId) {

        Project project = projectMapper.mapDtoToEntity(projectDto);
        projectRepository.save(project);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProject userProject = createUserProject(project, user);
        userProjectRepository.save(userProject);

        return userProjectMapper.mapEntityToDto(userProject);
    }

    private UserProject createUserProject(Project project, User user) {
        UserProject userProject = new UserProject();
        userProject.setProject(project);
        userProject.setUser(user);
        userProject.setRole(ProjectRole.OWNER);
        return userProject;
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
