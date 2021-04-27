package com.jesua.registration.mapper;

import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.service.CourseService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDtoFromEntity;
import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerEntityResponseDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FollowerMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private FollowerMapperImpl followerMapper;

    @Mock
    private CourseService courseService;

    @Mock
    private CourseMapper courseMapper;

    private static User user;
    private static Project project;

    @BeforeAll
    static void setUp(){
        project = buildProject(1);
        user = buildUserWithId(USER_ID);
    }

    @Test
    void mapDtoToEntityTest() {

        Course course = buildSavedCourse(2, user, 70, project);
        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower expectedFollower = buildFollowerFromDto(followerDto, course);

        doReturn(course).when(courseService).getCourse(course.getId());

        Follower actualFollower = followerMapper.mapDtoToEntity(followerDto);

        assertThat(actualFollower).usingRecursiveComparison().ignoringFields("token").isEqualTo(expectedFollower);
        assertThat(actualFollower.getToken()).isNotNull();
    }

    @Test
    void mapEntityToDtoTest() {

        Course course = buildSavedCourse(3, user, 80, project);
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course);
        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower follower = buildFollowerFromDto(followerDto, course);
        FollowerEntityResponseDto expectedFollowerEntityResponseDto = buildFollowerEntityResponseDto(follower, courseResponseDto);

        doReturn(courseResponseDto).when(courseMapper).mapEntityToDto(any());

        FollowerEntityResponseDto actualFollowerEntityResponseDto = followerMapper.mapEntityToDto(follower);

        assertThat(actualFollowerEntityResponseDto).usingRecursiveComparison().isEqualTo(expectedFollowerEntityResponseDto);

    }
}