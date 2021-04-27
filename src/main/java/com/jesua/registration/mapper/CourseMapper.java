package com.jesua.registration.mapper;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.service.ProjectService;
import com.jesua.registration.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static com.jesua.registration.util.AppUtil.stringToInstant;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class CourseMapper {

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", qualifiedByName = "InstantDateTime")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "project", source = "projectId")
    public abstract Course mapDtoToEntity(CourseDto courseDto);

    @Mapping(target = "startDate", qualifiedByName = "InstantDateTime")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "project", source = "projectId")
    public abstract Course mapDtoToEntity(CourseDto courseDto, @MappingTarget Course course);

    @Named("InstantDateTime")
    Instant instantString(String date) {
        return stringToInstant(date);
    }

    protected User mapIdtoUser(UUID userId) {
        return userService.getUser(userId);
    }

    @Mapping(source = "user", target = "createdBy")
    public abstract CourseResponseDto mapEntityToDto(Course course);

    protected Project mapIdtoProject(long id){
        return projectService.getProject(id);
    }
}
