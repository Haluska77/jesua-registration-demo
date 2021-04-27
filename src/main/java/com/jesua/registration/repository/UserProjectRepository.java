package com.jesua.registration.repository;

import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {

}
