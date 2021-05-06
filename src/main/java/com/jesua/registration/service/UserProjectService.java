package com.jesua.registration.service;

import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProjectService {

    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public String mapUserToProject(UUID userId, long projectId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);

        userProjectRepository.save(userProject);
        return "User has been successfully mapped to project";
    }

    public UserProject findById(UserProjectId userProjectId){
        return userProjectRepository.findById(userProjectId).orElseThrow(() -> new EntityNotFoundException("User Project link not found"));

    }
}
