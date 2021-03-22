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

public class CourseBuilder {

    public static Course buildCourse(){

        Course course = new Course();
        course.setDescription("desc");
        course.setStartDate(Instant.now());
        course.setCapacity(100);
        course.setOpen(true);
        course.setUser(buildUser());
        course.setCreated(Instant.now());

        return course;
    }

    public static Course buildSavedCourse(){

        Course course = new Course();
        course.setId(1);
        course.setDescription("desc");
        course.setStartDate(Instant.now());
        course.setCapacity(100);
        course.setOpen(true);
        course.setUser(buildUser());
        course.setCreated(Instant.now());

        return course;
    }

    public static Course buildCourse(boolean open, long delay, User user){

        Course course = new Course();
        course.setDescription("Test description");
        course.setStartDate(Instant.now().plus(Duration.ofDays(delay)));
        course.setCapacity(1000);
        course.setOpen(open);
        course.setUser(user);
        course.setCreated(Instant.now());

        return course;
    }

    public static CourseDto buildCourseDto(){

        CourseDto courseDto = new CourseDto();
        courseDto.setDescription("desc");
        courseDto.setStartDate(Instant.now().toString());
        courseDto.setCapacity(100);
        courseDto.setOpen(true);
        courseDto.setUserId(UUID.fromString("df46e040-a233-4333-8310-3bc83feb1cb3"));

        return courseDto;
    }

    public static CourseResponseDto buildCourseResponseDto(){

        CourseResponseDto courseResponseDto = new CourseResponseDto();
        courseResponseDto.setDescription("desc");
        courseResponseDto.setStartDate(Instant.now().toString());
        courseResponseDto.setCapacity(100);
        courseResponseDto.setOpen(true);
        courseResponseDto.setCreatedBy(buildUserResponseDto());
        courseResponseDto.setCreated(Instant.now().toString());

        return courseResponseDto;
    }
}
