package com.jesua.registration.service;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.mapper.CourseMapper;
import com.jesua.registration.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public Course getCourse(int id){
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

    public void deleteCourse(int id) {
        courseRepository.deleteById(id);
    }

    public CourseResponseDto updateCourse(CourseDto courseDto, int id) {

        Course savedCourse = courseRepository.getOne(id);
        Course course = courseMapper.mapDtoToEntity(courseDto, savedCourse);

        Course save = courseRepository.save(course);
        return courseMapper.mapEntityToDto(save);
    }
}
