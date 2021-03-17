package com.jesua.registration.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.jesua.registration.builder.CourseBuilder.buildCourse;
import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    CourseRepository courseRepository;

    @Mock
    CourseMapper courseMapper;

    @InjectMocks
    CourseService courseService;

    private Course course;

    @Test
    void getCourse() {
//        when(courseRepository.findById()).thenReturn();
    }

    @Test
    void getCourses() {
    }

    @Test
    void getActiveCourses() {
    }

    @Test
    void addCourse() {

        CourseDto courseDto = buildCourseDto();
        Course courseEntity = buildCourse();
        CourseResponseDto courseResponseDto = buildCourseResponseDto();

        when(courseMapper.mapDtoToEntity(courseDto)).thenReturn(courseEntity);
        when(courseRepository.save(any())).thenReturn(courseEntity);
        when(courseMapper.mapEntityToDto(courseEntity)).thenReturn(courseResponseDto);

        //run code
        CourseResponseDto actualResponseDto = courseService.addCourse(courseDto);

        //test
        verify(courseMapper).mapDtoToEntity(courseDto);
        verify(courseRepository).save(courseEntity);
        verify(courseMapper).mapEntityToDto(courseEntity);

        assertThat(actualResponseDto).isNotNull();
        assertThat(actualResponseDto).usingRecursiveComparison().isEqualTo(courseResponseDto);
    }

    @Test
    void updateCourse() {
    }
}