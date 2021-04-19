package com.jesua.registration.mapper;

import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper {


    @Mapping(target = "id", ignore = true)
    public abstract Project mapDtoToEntity(ProjectDto projectDto);

    public abstract Project mapDtoToEntity(ProjectDto projectDto, @MappingTarget Project project);

    public abstract ProjectResponseDto mapEntityToDto(Project project);
}
