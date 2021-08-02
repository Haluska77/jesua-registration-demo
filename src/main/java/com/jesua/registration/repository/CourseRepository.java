package com.jesua.registration.repository;

import com.jesua.registration.entity.Course;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    List<Course> findByStartDateBetween(Instant start, Instant end);

    List<Course> findAll(Specification<Course> spec);

    @Query("SELECT c FROM Course c where c.project IN (SELECT up.project FROM UserProject up WHERE up.user.id = :userid)")
    List<Course> findByUserProject(UUID userid);

    List<Course> findByImage(String contentId);
}
