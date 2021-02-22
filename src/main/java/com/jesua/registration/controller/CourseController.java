package com.jesua.registration.controller;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events/")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("addEvent")
    public CourseResponseDto addEvent(@RequestBody CourseDto courseDto) {

        return courseService.addCourse(courseDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("eventList")
    public List<CourseResponseDto> geEvents() {
        return courseService.getCourses();
    }

    @GetMapping("activeEventList")
    public List<CourseResponseDto> getActiveEvents() {
        return courseService.getActiveCourses();
    }

    @DeleteMapping("deleteEvent/{eventId}")
    public void deleteEvent(@PathVariable("eventId") int eventId) {
        courseService.deleteCourse(eventId);
    }

    @PostMapping("updateEvent/{eventId}")
    public CourseResponseDto updateEvent(@RequestBody CourseDto courseDto, @PathVariable("eventId") int eventId) {
        return courseService.updateCourse(courseDto, eventId);
    }

}