package com.jesua.registration.service;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.filter.CourseFilter;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.CourseSpecification;
import com.jesua.registration.repository.FollowerRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.FollowerBuilder.TOKEN;
import static com.jesua.registration.builder.FollowerBuilder.buildFullFollower;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private HomeService homeService;

    @Disabled("not able to test JPA specification. Functionality is tested in HomeControllerTest")
    @Test
    void getStatisticsTest() {
        Project project = buildProject(1);
        User user = buildUserWithId(USER_ID);

        Course course1 = buildSavedCourse(1, user, 10, project);
        Course course2 = buildSavedCourse(2, user, 50, project);
        List<Course> courseList = List.of(course1, course2);
        Follower existingFollower1 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course2);
        Follower existingFollower2 = buildFullFollower(UUID.randomUUID(), TOKEN, null, true, course2);
        Follower existingFollower3 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course1);
        Follower existingFollower4 = buildFullFollower(UUID.randomUUID(), TOKEN, null, false, course2);
        List<Follower> existingFollowerList = List.of(existingFollower1, existingFollower2, existingFollower3, existingFollower4);

        List<HashMap<String, Object>> expectedStat = courseList.stream().map(course -> {
            HashMap<String, Object> expStat = new HashMap<>();
            expStat.put("active", existingFollowerList.stream().filter(f -> f.getCourse().getId() == course.getId() && f.isAccepted()).count());
            expStat.put("waiting", existingFollowerList.stream().filter(f -> f.getCourse().getId() == course.getId() && !f.isAccepted() && f.getUnregistered() == null).count());
            expStat.put("id", course.getId());
            expStat.put("description", course.getDescription());
            expStat.put("startDate", course.getStartDate());
            expStat.put("capacity", course.getCapacity());
            expStat.put("image", course.getImage());
            expStat.put("project", course.getProject());
            return expStat;
        }).collect(Collectors.toList());

        Specification<Course> courseSpecification = new CourseSpecification(CourseFilter.builder().open(true).build());

        doReturn(List.of(course1, course2)).when(courseRepository).findAll(courseSpecification);
        doReturn(existingFollowerList).when(followerRepository).findByCourseIdIn(List.of(course1.getId(), course2.getId()));

        List<HomeService.Statistic> statistics = homeService.getStatistics();

        verify(courseRepository).findAll(courseSpecification);
        verify(followerRepository).findByCourseIdIn(List.of(course1.getId(), course2.getId()));

        assertThat(statistics).usingRecursiveComparison().isEqualTo(expectedStat);
    }
}