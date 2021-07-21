package com.jesua.registration.service;

import com.jesua.registration.entity.BasePrivateEntity;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.filter.CourseFilter;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.CourseSpecification;
import com.jesua.registration.repository.FollowerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final FollowerRepository followerRepository;
    private final CourseRepository courseRepository;

    public List<Statistic> getStatistics() {

        Specification<Course> courseSpecification = new CourseSpecification(
                CourseFilter.builder().open(true).startDate(Instant.now()).build());

        List<Course> courses = courseRepository.findAll(courseSpecification);
        List<Course> sortedCourseList = courses.stream()
                .sorted(Comparator.comparing(Course::getStartDate))
                .collect(toList());
        List<Long> courseIds = sortedCourseList.stream().map(BasePrivateEntity::getId).collect(toList());
        List<Follower> followerByOpenEvent = followerRepository.findByCourseIdIn(courseIds);

        return sortedCourseList.stream().map(course -> generateStatistic(followerByOpenEvent, course)).collect(toList());

    }

    private Statistic generateStatistic(List<Follower> followerByOpenEvent, Course course) {
        Statistic statistic = new Statistic();
        statistic.setActive(followerByOpenEvent.stream().filter(f -> f.getCourse().getId() == course.getId() && f.isAccepted()).count());
        statistic.setWaiting(followerByOpenEvent.stream().filter(f -> f.getCourse().getId() == course.getId() && !f.isAccepted() && f.getUnregistered() == null).count());
        statistic.setId(course.getId());
        statistic.setDescription(course.getDescription());
        statistic.setStartDate(course.getStartDate());
        statistic.setCapacity(course.getCapacity());
        statistic.setImage(course.getImage());
        statistic.setProject(course.getProject());

        return statistic;
    }

    @Setter
    @Getter
    public static class Statistic {
        private long active;
        private long waiting;
        private long id;
        private String description;
        private Instant startDate;
        private int capacity;
        private String image;
        private Project project;

    }
}
