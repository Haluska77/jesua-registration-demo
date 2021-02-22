package com.jesua.registration.repository;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, UUID> {

    List<Follower> findByCourse(Course course);

    //TODO fix to named query
    @Query("select f from Follower f join Course c on f.course = c.id where c.open=true")
    List<Follower> findFollowerByOpenEvent();

}
