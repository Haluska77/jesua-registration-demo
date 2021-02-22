package com.jesua.registration.mapper;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

import static com.jesua.registration.util.AppUtil.stringToInstant;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", qualifiedByName = "InstantDateTime")
    Course mapDtoToEntity(CourseDto courseDto);

    @Named("InstantDateTime")
    default Instant instantString(String date) {
        return stringToInstant(date);
    }

    CourseResponseDto mapEntityToDto(Course course);
}
