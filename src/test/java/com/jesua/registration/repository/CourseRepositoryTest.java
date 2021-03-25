package com.jesua.registration.repository;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.User;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCustomCourse;
import static com.jesua.registration.builder.UserBuilder.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseRepositoryTest {

    private static final UUID ID = UUID.randomUUID();

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Course openCourse;
    private Course closeCourse;

    @BeforeAll
    public void setUp(){
        savedUser = userRepository.save(buildUser(ID));
        openCourse = buildCustomCourse(true, 5, savedUser);
        courseRepository.save(openCourse);

        closeCourse = buildCustomCourse(false, 1, savedUser);
        courseRepository.save(closeCourse);

    }

    @Test
    void findById() {
        Course actualCourse = courseRepository.findById(openCourse.getId());

        assertThat(actualCourse).usingRecursiveComparison().ignoringFields("created", "startDate", "user").isEqualTo(openCourse);
        assertThat(actualCourse.getCreated()).isCloseTo(openCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualCourse.getStartDate()).isCloseTo(openCourse.getStartDate(), within(1, ChronoUnit.SECONDS));
        User user = (User) Hibernate.unproxy(actualCourse.getUser());
        assertThat(user).usingRecursiveComparison().ignoringFields("created", "passwordTokens").isEqualTo(openCourse.getUser());
        assertThat(user.getCreated()).isCloseTo(openCourse.getUser().getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(user.getPasswordTokens()).isEmpty();
    }

    @Test
    void findByOpenTrue() {
        List<Course> actualCourseList = courseRepository.findByOpenTrue();

        assertThat(actualCourseList.size()).isEqualTo(1);
        assertThat(actualCourseList.get(0)).usingRecursiveComparison().ignoringFields("created", "startDate", "user").isEqualTo(openCourse);
        assertThat(actualCourseList.get(0).getCreated()).isCloseTo(openCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualCourseList.get(0).getStartDate()).isCloseTo(openCourse.getStartDate(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void findByStartDateBetween() {
        List<Course> byStartDateBetween = courseRepository.findByStartDateBetween(Instant.now(), Instant.now().plus(Duration.ofDays(3)));
        assertThat(byStartDateBetween.size()).isEqualTo(1);
        assertThat(byStartDateBetween.get(0)).usingRecursiveComparison().ignoringFields("created", "startDate", "user").isEqualTo(closeCourse);
        assertThat(byStartDateBetween.get(0).getCreated()).isCloseTo(closeCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(byStartDateBetween.get(0).getStartDate()).isCloseTo(closeCourse.getStartDate(), within(1, ChronoUnit.SECONDS));
    }
}