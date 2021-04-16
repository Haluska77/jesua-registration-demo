package com.jesua.registration.repository;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCustomCourse;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowerRepositoryTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User savedUser;
    private Course course1;
    private Course course2;
    private Follower savedFollower1;
    private Follower savedFollower2;
    private Project project;

    @BeforeAll
    public void setUp() {

        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        savedUser = buildUserWithOutId(project);
        userRepository.save(savedUser);

        course1 = buildCustomCourse(true, 5, savedUser, project);
        course2 = buildCustomCourse(false, 10, savedUser, project);

        courseRepository.save(course1);
        courseRepository.save(course2);

        FollowerDto followerDto = buildFollowerDto(1);

        savedFollower1 = buildFollowerFromDto(followerDto, course1);
        savedFollower1.setAccepted(true);
        followerRepository.save(savedFollower1);

        savedFollower2 = buildFollowerFromDto(followerDto, course2);
        savedFollower2.setAccepted(true);
        followerRepository.save(savedFollower2);
    }

    @AfterAll
    public void tearDown() {
        followerRepository.delete(savedFollower1);
        followerRepository.delete(savedFollower2);
        courseRepository.delete(course1);
        courseRepository.delete(course2);
        userRepository.delete(savedUser);
        projectRepository.delete(project);
    }

    @Test
    void findByCourseTest() {
        List<Follower> byCourse = followerRepository.findByCourseId(course2.getId());

        assertEquals(1, byCourse.size());
        assertEquals(byCourse.get(0).getId(), savedFollower2.getId());
    }

    @Test
    void findFollowerByOpenCourseTest() {

        List<Follower> byCourse = followerRepository.findByCourseOpen(true);

        assertEquals(1, byCourse.size());
        assertEquals(byCourse.get(0).getId(), savedFollower1.getId());
    }
}