package com.jesua.registration.service;

import com.jesua.registration.dto.ProjectDetailDto;
import com.jesua.registration.dto.UserProjectDetailDto;
import com.jesua.registration.dto.UserProjectIdResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.mapper.UserProjectMapper;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserProjectService {

    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectMapper userProjectMapper;

    public String mapUserToProject(UUID userId, long projectId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);

        userProjectRepository.save(userProject);
        return "User has been successfully mapped to project";
    }

    public UserProject findById(UserProjectId userProjectId) {
        return userProjectRepository.findById(userProjectId).orElseThrow(() -> new EntityNotFoundException("User Project link not found"));

    }

    public List<ProjectDetailDto> getAllUserProjectsDetail() {
        List<UserProjectResponseDto> collect = userProjectRepository.findAll()
                .stream()
                .map(userProjectMapper::mapEntityToDto)
                .collect(toList());
        return buildProjectsDetailList(collect);
    }

    public ProjectDetailDto getUserProjectDetailByProject(long projectId) {
        List<UserProjectResponseDto> userProject = userProjectRepository.findByProjectId(projectId)
                .stream()
                .map(userProjectMapper::mapEntityToDto)
                .collect(toList());

        if (userProject.isEmpty()) {
            throw new EntityNotFoundException("Projekt neexistuje!");
        }
        return buildProjectsDetailList(userProject).get(0);
    }

    private List<ProjectDetailDto> buildProjectsDetailList(List<UserProjectResponseDto> userProjectList) {
        return userProjectList.stream()
                .collect(groupingBy(p -> p.getProject().getId()))
                .values().stream()
                .map(userProjectResponseDtos -> new ProjectDetailDto(userProjectResponseDtos.get(0).getProject(), userProjectResponseDtos.stream()
                                .map(user ->
                                        new UserProjectDetailDto(user.getUser(), user.getRole(), user.getCreated())
                                ).collect(toList())
                        )
                ).collect(toList());
    }

    public List<UserProjectIdResponseDto> getUserProjectIdList() {
        return userProjectRepository.findAll()
                .stream()
                .map(userProjectMapper::mapEntityToDtoIds)
                .collect(toList());
    }
}
