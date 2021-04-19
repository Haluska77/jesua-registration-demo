package com.jesua.registration.mapper;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDtoFromEntity;
import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CourseMapperTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private CourseMapperImpl courseMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserMapperImpl userMapper;

    private static User user;
    private static User savedUser;
    private static Project project;

    @BeforeAll
    static void setUp(){
        project = buildProject(1);
        user = buildUserWithId(USER_ID, project);
        savedUser = buildUserWithId(USER_ID, project);
    }

    @Test
    void mapDtoToEntityTest() {

        CourseDto courseDto = buildCourseDto(USER_ID, project.getId());
        Course expectedCourse = buildCourseFromDto(courseDto, user, project);

        doReturn(user).when(userService).getUser(USER_ID);
        doReturn(project).when(userMapper).getProject(project.getId());

        Course actualCourse = courseMapper.mapDtoToEntity(courseDto);

        assertThat(actualCourse).usingRecursiveComparison().isEqualTo(expectedCourse);

    }

    @Test
    void mapDtoToExistingEntityTest() {

        Course savedCourse = buildSavedCourse(3, savedUser, 80, project);
        CourseDto courseDto = buildCourseDto(USER_ID, project.getId());
        Course expectedCourse = buildCourseFromDto(courseDto, savedCourse, savedUser, project);

        doReturn(user).when(userService).getUser(USER_ID);
        doReturn(project).when(userMapper).getProject(project.getId());

        Course actualCourse = courseMapper.mapDtoToEntity(courseDto, savedCourse);

        assertThat(actualCourse).usingRecursiveComparison().isEqualTo(expectedCourse);
    }

    @Test
    void mapEntityToResponseDtoTest() {

        UserResponseDto userResponseDto = buildUserResponseDtoFromEntity(user);
        Course course = buildSavedCourse(3, user, 80, project);
        CourseResponseDto expectedCourseResponseDto = buildCourseResponseDtoFromEntity(course);

        doReturn(userResponseDto).when(userMapper).mapEntityToDto(any());

        CourseResponseDto actualCourseResponseDto = courseMapper.mapEntityToDto(course);

        assertThat(actualCourseResponseDto).usingRecursiveComparison().isEqualTo(expectedCourseResponseDto);
    }
}