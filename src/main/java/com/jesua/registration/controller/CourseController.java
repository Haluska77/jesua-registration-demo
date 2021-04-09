package com.jesua.registration.controller;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("events/")
public class CourseController {

    private final CourseService courseService;

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