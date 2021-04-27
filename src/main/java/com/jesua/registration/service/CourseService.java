package com.jesua.registration.service;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.mapper.CourseMapper;
import com.jesua.registration.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public Course getCourse(long id){
        return courseRepository.findById(id)
                .orElse(null);
    }

    public List<CourseResponseDto> getCourses(){

        return courseRepository.findAll()
                .stream().map(courseMapper::mapEntityToDto).collect(Collectors.toList());
    }

    public List<CourseResponseDto> getActiveCourses(){
        return courseRepository.findByOpenTrue()
                .stream().map(courseMapper::mapEntityToDto).collect(Collectors.toList());
    }

    public CourseResponseDto addCourse(CourseDto courseDto) {

        Course course = courseMapper.mapDtoToEntity(courseDto);

        Course save = courseRepository.save(course);

        return courseMapper.mapEntityToDto(save);
    }

    public void deleteCourse(long id) {
        courseRepository.deleteById(id);
    }

    public CourseResponseDto updateCourse(CourseDto courseDto, long id) {

        Course savedCourse = courseRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Course not found!!!"));
        Course course = courseMapper.mapDtoToEntity(courseDto, savedCourse);

        Course save = courseRepository.save(course);
        return courseMapper.mapEntityToDto(save);
    }
}
