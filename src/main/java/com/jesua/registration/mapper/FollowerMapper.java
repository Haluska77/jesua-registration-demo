package com.jesua.registration.mapper;

import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.service.CourseService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.jesua.registration.util.AppUtil.generateToken;

@Mapper(componentModel = "spring", imports = {Instant.class}, uses = CourseMapper.class)
@Component
public abstract class FollowerMapper {

    @Autowired
    private CourseService courseService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "course", source = "eventId", qualifiedByName = "course")
    @Mapping(target = "registered", expression  = "java(Instant.now())")
    public abstract Follower mapDtoToEntity(FollowerDto followerDto);

    @Named("course")
    Course mapCourse(int eventId) {
        return courseService.getCourse(eventId);
    }

    @AfterMapping
    public void mapToken( @MappingTarget Follower follower) {
        follower.setToken(generateToken());
    }

    public abstract FollowerEntityResponseDto mapEntityToDto(Follower follower);
}
