package com.jesua.registration.mapper;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class})
public abstract class ProjectMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(Instant.now())")
    public abstract Project mapDtoToEntity(ProjectDto projectDto);

    public abstract Project mapDtoToEntity(ProjectDto projectDto, @MappingTarget Project project);

    public abstract ProjectResponseDto mapEntityToDto(Project project);
}
