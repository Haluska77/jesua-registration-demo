package com.jesua.registration.controller;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.filter.CourseFilter;
import com.jesua.registration.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("events/")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("addEvent")
    public CourseResponseDto addEvent(@RequestBody CourseDto courseDto) {
        return courseService.addCourse(courseDto);
    }

    @GetMapping("eventList")
    public List<CourseResponseDto> getEvents(CourseFilter courseFilter) {
        return courseService.getCourses(courseFilter);
    }

    @GetMapping("eventListByUserProject/{userId}")
    public List<CourseResponseDto> getEventsByUserProject(@PathVariable("userId") UUID userId) {
        return courseService.getCoursesByUserProject(userId);
    }

    @PostMapping("updateEvent/{eventId}")
    public CourseResponseDto updateEvent(@RequestBody CourseDto courseDto, @PathVariable("eventId") int eventId) {
        return courseService.updateCourse(courseDto, eventId);
    }

}