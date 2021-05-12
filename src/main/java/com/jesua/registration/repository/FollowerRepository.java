package com.jesua.registration.repository;

import com.jesua.registration.entity.Follower;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID>, JpaSpecificationExecutor<Follower> {

    List<Follower> findByCourseId(long courseId);

    List<Follower> findByCourseIdIn(List<Long> ids);

    List<Follower> findAll(Specification<Follower> spec);

}
