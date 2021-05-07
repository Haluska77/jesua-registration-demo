package com.jesua.registration.service;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.filter.CourseFilter;
import com.jesua.registration.mapper.CourseMapper;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public Course getCourse(long id) {
        return courseRepository.findById(id)
                .orElse(null);
    }

    public List<CourseResponseDto> getCourses(CourseFilter courseFilter) {

        return courseRepository.findAll(new CourseSpecification(courseFilter))
                .stream().map(courseMapper::mapEntityToDto).collect(Collectors.toList());
    }

    public CourseResponseDto addCourse(CourseDto courseDto) {

        Course course = courseMapper.mapDtoToEntity(courseDto);

        Course save = courseRepository.save(course);

        return courseMapper.mapEntityToDto(save);
    }

    public CourseResponseDto updateCourse(CourseDto courseDto, long id) {

        Course savedCourse = courseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found!!!"));
        Course course = courseMapper.mapDtoToEntity(courseDto, savedCourse);

        Course save = courseRepository.save(course);
        return courseMapper.mapEntityToDto(save);
    }

    public List<CourseResponseDto> getCoursesByUserProject(UUID userId) {
        return courseRepository.findByUserProject(userId)
                .stream().map(courseMapper::mapEntityToDto).collect(Collectors.toList());
    }
}
