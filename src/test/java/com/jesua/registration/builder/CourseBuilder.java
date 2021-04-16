package com.jesua.registration.builder;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDtoFromEntity;
import static com.jesua.registration.util.AppUtil.stringToInstant;

public class CourseBuilder {

    public static Course buildCourseFromDto(CourseDto courseDto, User user, Project project){

        Course course = new Course();
        course.setDescription(courseDto.getDescription());
        course.setStartDate(stringToInstant(courseDto.getStartDate()));
        course.setCapacity(courseDto.getCapacity());
        course.setOpen(courseDto.isOpen());
        course.setUser(user);
        course.setCreated(Instant.now());
        course.setImage(courseDto.getImage());
        course.setProject(project);

        return course;
    }

    public static Course buildCourseFromDto(CourseDto courseDto, Course course, User user, Project project){

        course.setDescription(courseDto.getDescription());
        course.setStartDate(stringToInstant(courseDto.getStartDate()));
        course.setCapacity(courseDto.getCapacity());
        course.setOpen(courseDto.isOpen());
        course.setUser(user);
        course.setCreated(Instant.now());
        course.setImage(courseDto.getImage());
        course.setProject(project);

        return course;
    }

    public static Course buildSavedCourse(int id, User user, int capacity, Project project){

        Course course = new Course();
        course.setId(id);
        course.setDescription("saved description");
        course.setStartDate(Instant.now());
        course.setCapacity(capacity);
        course.setOpen(true);
        course.setUser(user);
        course.setCreated(Instant.now());
        course.setImage("logo");
        course.setProject(project);

        return course;
    }

    public static Course buildCustomCourse(boolean open, long startDateDelay, User user, Project project){

        Course course = new Course();
        course.setDescription("Test description");
        course.setStartDate(Instant.now().plus(Duration.ofDays(startDateDelay)));
        course.setCapacity(1000);
        course.setOpen(open);
        course.setUser(user);
        course.setCreated(Instant.now());
        course.setImage("my logo");
        course.setProject(project);

        return course;
    }

    // input course dto from UI
    public static CourseDto buildCourseDto(UUID userId, int projectId){

        CourseDto courseDto = new CourseDto();
        courseDto.setDescription("desc");
        courseDto.setStartDate("2021-05-01T15:00");
        courseDto.setCapacity(100);
        courseDto.setOpen(true);
        courseDto.setUserId(userId);
        courseDto.setImage("logo");
        courseDto.setProjectId(projectId);

        return courseDto;
    }

    // output course submitted to UI
    public static CourseResponseDto buildCourseResponseDtoFromEntity(Course course){

        CourseResponseDto courseResponseDto = new CourseResponseDto();
        courseResponseDto.setId(course.getId());
        courseResponseDto.setDescription(course.getDescription());
        courseResponseDto.setStartDate(course.getStartDate().toString());
        courseResponseDto.setCapacity(course.getCapacity());
        courseResponseDto.setOpen(course.getOpen());
        courseResponseDto.setCreatedBy(buildUserResponseDtoFromEntity(course.getUser()));
        courseResponseDto.setCreated(course.getCreated());
        courseResponseDto.setImage(course.getImage());
        courseResponseDto.setProject(buildProjectResponseDtoFromEntity(course.getProject()));

        return courseResponseDto;
    }
}
