package com.jesua.registration.service;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final FollowerRepository followerRepository;
    private final CourseRepository courseRepository;

    public List<Map<String, Object>> getStatistics() {

        List<Course> courses = courseRepository.findByOpenTrue();
        List<Follower> followerByOpenEvent = followerRepository.findFollowerByOpenEvent();

        return courses.stream().map(e -> {
            Map<String, Object> followerCount = new HashMap<>();
            followerCount.put("active", followerByOpenEvent.stream().filter(f -> f.getCourse().getId() == e.getId() && f.isAccepted()).count());
            followerCount.put("waiting", followerByOpenEvent.stream().filter(f -> f.getCourse().getId() == e.getId() && !f.isAccepted() && f.getUnregistered() == null).count());
            followerCount.put("id", e.getId());
            followerCount.put("description", e.getDescription());
            followerCount.put("startDate", e.getStartDate());
            followerCount.put("capacity", e.getCapacity());

            return followerCount;
        }).collect(toList());

    }
}
