package com.jesua.registration.service;

import com.jesua.registration.builder.CourseBuilder;
import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.mapper.CourseMapper;
import com.jesua.registration.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDto;
import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getCoursesTest() {
        Course course1 = buildSavedCourse(1, USER_ID,  20);
        List<Course> courses = List.of(course1);
        CourseResponseDto courseResponseDto = buildCourseResponseDto(course1);

        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.mapEntityToDto(courses.get(0))).thenReturn(courseResponseDto);

        List<CourseResponseDto> actualResponseDto = courseService.getCourses();

        verify(courseRepository).findAll();
        verify(courseMapper).mapEntityToDto(course1);

        assertThat(actualResponseDto.get(0)).isNotNull();
        assertThat(actualResponseDto.get(0)).usingRecursiveComparison().isEqualTo(courseResponseDto);
    }

    @Test
    void getActiveCoursesTest() {
        Course course1 = buildSavedCourse(1, USER_ID, 80);
        List<Course> courses = List.of(course1);
        CourseResponseDto courseResponseDto = buildCourseResponseDto(course1);

        when(courseRepository.findByOpenTrue()).thenReturn(courses);
        when(courseMapper.mapEntityToDto(courses.get(0))).thenReturn(courseResponseDto);

        List<CourseResponseDto> actualResponseDto = courseService.getActiveCourses();

        verify(courseRepository).findByOpenTrue();
        verify(courseMapper).mapEntityToDto(course1);

        assertThat(actualResponseDto.get(0)).isNotNull();
        assertThat(actualResponseDto.get(0)).usingRecursiveComparison().isEqualTo(courseResponseDto);

    }

    @Test
    void addCourseTest() {

        CourseDto courseDto = buildCourseDto();
        Course courseEntity = CourseBuilder.buildCourseFromDto(courseDto);
        CourseResponseDto courseResponseDto = buildCourseResponseDto(courseEntity);

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
        CourseDto courseDto = buildCourseDto();
        Course savedCourse = buildSavedCourse(1, USER_ID, 50);
        Course updatedCourse = buildCourseFromDto(courseDto, savedCourse);
        CourseResponseDto courseResponseDto = buildCourseResponseDto(updatedCourse);

        when(courseRepository.getOne(any())).thenReturn(savedCourse);
        when(courseMapper.mapDtoToEntity(courseDto, savedCourse)).thenReturn(updatedCourse);
        when(courseRepository.save(any())).thenReturn(updatedCourse);
        when(courseMapper.mapEntityToDto(updatedCourse)).thenReturn(courseResponseDto);

        CourseResponseDto actualResponseDto = courseService.updateCourse(courseDto, 1);

        verify(courseRepository).getOne(1);
        verify(courseMapper).mapDtoToEntity(courseDto, savedCourse);
        verify(courseRepository).save(updatedCourse);
        verify(courseMapper).mapEntityToDto(updatedCourse);

        assertThat(actualResponseDto).isNotNull();
        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(courseResponseDto);

    }
}