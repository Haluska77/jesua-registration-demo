package com.jesua.registration.mapper;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.User;
import com.jesua.registration.service.CourseService;
import com.jesua.registration.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static com.jesua.registration.util.AppUtil.stringToInstant;

@Mapper(componentModel = "spring", imports = {Instant.class}, uses = UserMapper.class)
public abstract class CourseMapper {

    @Autowired
    UserService userService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", qualifiedByName = "InstantDateTime")
    @Mapping(target = "created", expression  = "java(Instant.now())")
    @Mapping(target = "user", source = "userId", qualifiedByName = "user")
    public abstract Course mapDtoToEntity(CourseDto courseDto);

    @Mapping(target = "startDate", qualifiedByName = "InstantDateTime")
    @Mapping(target = "user", source = "userId", qualifiedByName = "user")
    public abstract Course mapDtoToEntity(CourseDto courseDto, @MappingTarget Course course);

    @Named("InstantDateTime")
    Instant instantString(String date) {
        return stringToInstant(date);
    }

    @Named("user")
    User mapUser(UUID userId) {
        return userService.getUser(userId);
    }

    @Mapping(source = "user", target = "createdBy")
    public abstract CourseResponseDto mapEntityToDto(Course course);
}
