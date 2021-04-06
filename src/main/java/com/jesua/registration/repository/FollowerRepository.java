package com.jesua.registration.repository;

import com.jesua.registration.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID> {

    List<Follower> findByCourseId(int courseId);

    List<Follower> findByCourseOpen(boolean open);

}
