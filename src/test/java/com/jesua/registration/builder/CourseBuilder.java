package com.jesua.registration.builder;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.User;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.jesua.registration.builder.UserBuilder.buildUser;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDto;
import static com.jesua.registration.util.AppUtil.stringToInstant;

public class CourseBuilder {

    public static Course buildCourseFromDto(CourseDto courseDto){

        Course course = new Course();
        course.setDescription(courseDto.getDescription());
        course.setStartDate(stringToInstant(courseDto.getStartDate()));
        course.setCapacity(courseDto.getCapacity());
        course.setOpen(courseDto.isOpen());
        course.setUser(buildUser(courseDto.getUserId()));
        course.setCreated(Instant.now());

        return course;
    }

    public static Course buildCourseFromDto(CourseDto courseDto, Course course){

        course.setDescription(courseDto.getDescription());
        course.setStartDate(stringToInstant(courseDto.getStartDate()));
        course.setCapacity(courseDto.getCapacity());
        course.setOpen(courseDto.isOpen());
        course.setUser(buildUser(courseDto.getUserId()));
        course.setCreated(Instant.now());

        return course;
    }

    public static Course buildSavedCourse(int id, UUID userId, int capacity){

        Course course = new Course();
        course.setId(id);
        course.setDescription("saved description");
        course.setStartDate(Instant.now());
        course.setCapacity(capacity);
        course.setOpen(true);
        course.setUser(buildUser(userId));
        course.setCreated(Instant.now());

        return course;
    }

    public static Course buildCustomCourse(boolean open, long startDateDelay, User user){

        Course course = new Course();
        course.setDescription("Test description");
        course.setStartDate(Instant.now().plus(Duration.ofDays(startDateDelay)));
        course.setCapacity(1000);
        course.setOpen(open);
        course.setUser(user);
        course.setCreated(Instant.now());

        return course;
    }

    // input course dto from UI
    public static CourseDto buildCourseDto(){

        CourseDto courseDto = new CourseDto();
        courseDto.setDescription("desc");
        courseDto.setStartDate("2021-05-01T15:00");
        courseDto.setCapacity(100);
        courseDto.setOpen(true);
        courseDto.setUserId(UUID.fromString("df46e040-a233-4333-8310-3bc83feb1cb3"));

        return courseDto;
    }

    // output course submitted to UI
    public static CourseResponseDto buildCourseResponseDto(Course course){

        CourseResponseDto courseResponseDto = new CourseResponseDto();
        courseResponseDto.setId(course.getId());
        courseResponseDto.setDescription(course.getDescription());
        courseResponseDto.setStartDate(course.getStartDate().toString());
        courseResponseDto.setCapacity(course.getCapacity());
        courseResponseDto.setOpen(course.getOpen());
        courseResponseDto.setCreatedBy(buildUserResponseDto(course.getUser()));
        courseResponseDto.setCreated(course.getCreated().toString());

        return courseResponseDto;
    }
}
