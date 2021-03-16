package com.jesua.registration.repository;

import com.jesua.registration.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    Course findById(int id);
    List<Course> findByOpenTrue();
    List<Course> findByStartDateBetween(Instant start, Instant end);

}
