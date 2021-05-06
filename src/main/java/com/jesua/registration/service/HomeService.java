package com.jesua.registration.service;

import com.jesua.registration.entity.BasePrivateEntity;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jesua.registration.repository.CourseNewSpecification.courseHasStatusAndIsAfter;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final FollowerRepository followerRepository;
    private final CourseRepository courseRepository;

    public List<Map<String, Object>> getStatistics() {

        List<Course> courses = courseRepository.findAll(courseHasStatusAndIsAfter(true, Instant.now()));
        List<Long> courseIds = courses.stream().map(BasePrivateEntity::getId).collect(toList());
        List<Follower> followerByOpenEvent = followerRepository.findByCourseIdIn(courseIds);

        return courses.stream().map(course -> generateStatistics(followerByOpenEvent, course)).collect(toList());

    }

    private Map<String, Object> generateStatistics(List<Follower> followerByOpenEvent, Course course) {
        Map<String, Object> followerCount = new HashMap<>();
        followerCount.put("active", followerByOpenEvent.stream().filter(f -> f.getCourse().getId() == course.getId() && f.isAccepted()).count());
        followerCount.put("waiting", followerByOpenEvent.stream().filter(f -> f.getCourse().getId() == course.getId() && !f.isAccepted() && f.getUnregistered() == null).count());
        followerCount.put("id", course.getId());
        followerCount.put("description", course.getDescription());
        followerCount.put("startDate", course.getStartDate());
        followerCount.put("capacity", course.getCapacity());
        followerCount.put("image", course.getImage());
        followerCount.put("project", course.getProject());

        return followerCount;
    }
}
