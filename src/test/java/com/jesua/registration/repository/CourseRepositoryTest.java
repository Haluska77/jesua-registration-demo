package com.jesua.registration.repository;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.jesua.registration.builder.CourseBuilder.buildCustomCourse;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User user;
    private Course openCourse;
    private Course closeCourse;
    private Project project;

    @BeforeAll
    public void setUp(){

        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        user = buildUserWithOutId();
        userRepository.save(user);

        openCourse = buildCustomCourse(true, 5, user, project);
        courseRepository.save(openCourse);

        closeCourse = buildCustomCourse(false, 1, user, project);
        courseRepository.save(closeCourse);

    }

    @AfterAll
    public void tearDown(){
        courseRepository.delete(openCourse);
        courseRepository.delete(closeCourse);
        userRepository.delete(user);
        projectRepository.delete(project);
    }

    @Test
    void findByIdTest() {
        Course actualCourse = courseRepository.findById(openCourse.getId()).orElseGet(()-> fail("Course not found"));

        assertThat(actualCourse).usingRecursiveComparison().ignoringFields("created", "startDate", "user", "project").isEqualTo(openCourse);
        assertThat(actualCourse.getCreated()).isCloseTo(openCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualCourse.getStartDate()).isCloseTo(openCourse.getStartDate(), within(1, ChronoUnit.SECONDS));
        User user = (User) Hibernate.unproxy(actualCourse.getUser());
        assertThat(user).usingRecursiveComparison().ignoringFields("created", "passwordTokens", "project").isEqualTo(openCourse.getUser());
        assertThat(user.getCreated()).isCloseTo(openCourse.getUser().getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(user.getPasswordTokens()).isEmpty();
        //FIXME to resolve no Session for nested objects
//        Project project = (Project) Hibernate.unproxy(actualCourse.getUser().getProject());
//        assertThat(project).usingRecursiveComparison().ignoringFields("created").isEqualTo(openCourse.getUser().getProject());

    }

    @Test
    void findByOpenTrueTest() {
        List<Course> actualCourseList = courseRepository.findByOpenTrue();

        assertThat(actualCourseList.size()).isEqualTo(1);
        assertThat(actualCourseList.get(0)).usingRecursiveComparison().ignoringFields("created", "startDate", "user", "project").isEqualTo(openCourse);
        assertThat(actualCourseList.get(0).getCreated()).isCloseTo(openCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualCourseList.get(0).getStartDate()).isCloseTo(openCourse.getStartDate(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void findByStartDateBetweenTest() {
        List<Course> byStartDateBetween = courseRepository.findByStartDateBetween(Instant.now(), Instant.now().plus(Duration.ofDays(3)));
        assertThat(byStartDateBetween.size()).isEqualTo(1);
        assertThat(byStartDateBetween.get(0)).usingRecursiveComparison().ignoringFields("created", "startDate", "user", "project").isEqualTo(closeCourse);
        assertThat(byStartDateBetween.get(0).getCreated()).isCloseTo(closeCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(byStartDateBetween.get(0).getStartDate()).isCloseTo(closeCourse.getStartDate(), within(1, ChronoUnit.SECONDS));
    }
}