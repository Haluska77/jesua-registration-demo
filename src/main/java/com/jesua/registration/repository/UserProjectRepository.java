package com.jesua.registration.repository;

import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {

    List<UserProject> findByProjectId(long id);
    List<UserProject> findByUserId(UUID id);
}
