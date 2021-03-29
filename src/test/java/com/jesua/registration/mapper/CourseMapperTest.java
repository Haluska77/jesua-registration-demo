package com.jesua.registration.mapper;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.User;
import com.jesua.registration.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDtoFromEntity;
import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.UserBuilder.buildUser;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
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

    @Test
    void mapDtoToEntityTest() {
        User user = buildUser(USER_ID);
        CourseDto courseDto = buildCourseDto(USER_ID);
        Course expectedCourse = buildCourseFromDto(courseDto);

        doReturn(user).when(userService).getUser(USER_ID);

        Course actualCourse = courseMapper.mapDtoToEntity(courseDto);

        assertThat(actualCourse).usingRecursiveComparison().ignoringFields("created", "user.created").isEqualTo(expectedCourse);
        assertThat(actualCourse.getCreated()).isCloseTo(expectedCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualCourse.getUser().getCreated()).isCloseTo(expectedCourse.getUser().getCreated(), within(1, ChronoUnit.SECONDS));

    }

    @Test
    void mapDtoToExistingEntityTest() {
        User user = buildUser(USER_ID);
        Course course = buildSavedCourse(3, USER_ID, 80);
        CourseDto courseDto = buildCourseDto(USER_ID);
        Course expectedCourse = buildCourseFromDto(courseDto);

        doReturn(user).when(userService).getUser(USER_ID);

        Course actualCourse = courseMapper.mapDtoToEntity(courseDto, course);

        assertThat(actualCourse).usingRecursiveComparison().ignoringFields("id", "created", "user.created").isEqualTo(expectedCourse);
        assertThat(actualCourse.getId()).isEqualTo(course.getId());
        assertThat(actualCourse.getCreated()).isCloseTo(expectedCourse.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(actualCourse.getUser().getCreated()).isCloseTo(expectedCourse.getUser().getCreated(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void mapEntityToDtoTest() {

        UserResponseDto userResponseDto = buildUserResponseDto(buildUser(USER_ID));
        Course course = buildSavedCourse(3, USER_ID, 80);
        CourseResponseDto expectedCourseResponseDto = buildCourseResponseDtoFromEntity(course);

        doReturn(userResponseDto).when(userMapper).mapEntityToDto(any());

        CourseResponseDto actualCourseResponseDto = courseMapper.mapEntityToDto(course);

        assertThat(actualCourseResponseDto).usingRecursiveComparison().ignoringFields("createdBy.created").isEqualTo(expectedCourseResponseDto);
        assertThat(actualCourseResponseDto.getCreatedBy().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getCreated(), within(1, ChronoUnit.SECONDS));
    }
}