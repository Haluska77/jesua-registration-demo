package com.jesua.registration.service;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.filter.CourseFilter;
import com.jesua.registration.mapper.CourseMapper;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.CourseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDtoFromEntity;
import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    @Mock
    CourseRepository courseRepository;

    @Mock
    CourseMapper courseMapper;

    @InjectMocks
    CourseService courseService;

    private static User user;
    private static Project project;
    private static CourseDto courseDto;

    @BeforeAll
    static void setUp(){
        project = buildProject(1);
        user = buildUserWithId(USER_ID);
        courseDto = buildCourseDto(USER_ID, project.getId());
    }

    @Disabled("not able to test JPA specification. Functionality is tested in CourseControllerTest")
    @Test
    void getCoursesTest() {

        Course course1 = buildSavedCourse(1, user,  20, project);
        List<Course> courses = List.of(course1);
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course1);

        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.mapEntityToDto(courses.get(0))).thenReturn(courseResponseDto);

        List<CourseResponseDto> actualResponseDto = courseService.getCourses(CourseFilter.builder().build());

        verify(courseRepository).findAll();
        verify(courseMapper).mapEntityToDto(course1);

        assertThat(actualResponseDto.get(0)).isNotNull();
        assertThat(actualResponseDto.get(0)).usingRecursiveComparison().isEqualTo(courseResponseDto);
    }

    @Disabled("not able to test JPA specification. Functionality is tested in CourseControllerTest")
    @Test
    void getActiveCoursesTest() {

        Course course1 = buildSavedCourse(1, user, 80, project);
        List<Course> courses = List.of(course1);
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(course1);

        Specification<Course> courseSpecification = new CourseSpecification(CourseFilter.builder().open(true).build());

        doReturn(courses).when(courseRepository).findAll(courseSpecification);
        when(courseMapper.mapEntityToDto(courses.get(0))).thenReturn(courseResponseDto);

        List<CourseResponseDto> actualResponseDto = courseService.getCourses(CourseFilter.builder().open(true).build());

        verify(courseRepository).findAll(courseSpecification);
        verify(courseMapper).mapEntityToDto(course1);

        assertThat(actualResponseDto.get(0)).isNotNull();
        assertThat(actualResponseDto.get(0)).usingRecursiveComparison().isEqualTo(courseResponseDto);

    }

    @Test
    void addCourseTest() {

        Course courseEntity = buildCourseFromDto(courseDto, user, project);
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(courseEntity);

        when(courseMapper.mapDtoToEntity(courseDto)).thenReturn(courseEntity);
        when(courseRepository.save(any())).thenReturn(courseEntity);
        when(courseMapper.mapEntityToDto(courseEntity)).thenReturn(courseResponseDto);

        //run code
        CourseResponseDto courseResponseDto1 = courseService.addCourse(courseDto);

        //test
        verify(courseMapper).mapDtoToEntity(courseDto);
        verify(courseRepository).save(courseEntity);
        verify(courseMapper).mapEntityToDto(courseEntity);

        assertThat(courseResponseDto1).isNotNull();
        assertThat(courseResponseDto1).usingRecursiveComparison().isEqualTo(courseResponseDto);
    }

    @Test
    void updateCourseTest() {

        Course savedCourse = buildSavedCourse(1, user, 50, project);
        Course updatedCourse = buildCourseFromDto(courseDto, savedCourse, user, project);
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(updatedCourse);

        doReturn(Optional.of(savedCourse)).when(courseRepository).findById(1L);
        when(courseMapper.mapDtoToEntity(courseDto, savedCourse)).thenReturn(updatedCourse);
        when(courseRepository.save(any())).thenReturn(updatedCourse);
        when(courseMapper.mapEntityToDto(updatedCourse)).thenReturn(courseResponseDto);

        CourseResponseDto actualResponseDto = courseService.updateCourse(courseDto, 1);

        verify(courseRepository).findById(1L);
        verify(courseMapper).mapDtoToEntity(courseDto, savedCourse);
        verify(courseRepository).save(updatedCourse);
        verify(courseMapper).mapEntityToDto(updatedCourse);

        assertThat(actualResponseDto).isNotNull();
        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(courseResponseDto);

    }

    @Test
    void getCoursesByUserProjectTest() {

        Course savedCourse = buildSavedCourse(1, user, 50, project);
        CourseResponseDto courseResponseDto = buildCourseResponseDtoFromEntity(savedCourse);

        doReturn(List.of(savedCourse)).when(courseRepository).findByUserProject(USER_ID);
        doReturn(courseResponseDto).when(courseMapper).mapEntityToDto(savedCourse);

        List<CourseResponseDto> actualResponseDto = courseService.getCoursesByUserProject(USER_ID);

        verify(courseRepository).findByUserProject(USER_ID);
        verify(courseMapper).mapEntityToDto(savedCourse);

        assertThat(actualResponseDto).isNotNull();
        assertThat(actualResponseDto.get(0)).usingRecursiveComparison().isEqualTo(courseResponseDto);

    }
}