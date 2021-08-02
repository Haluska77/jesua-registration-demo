package com.jesua.registration.mapper;

import com.jesua.registration.dto.UserProjectIdResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.UserProject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class UserProjectMapper {

    public abstract UserProjectResponseDto mapEntityToDto(UserProject project);

    public abstract Set<UserProjectResponseDto> mapEntitySetToDtoSet(Set<UserProject> userProject);

    @Mapping(source = "project.id", target = "project")
    @Mapping(source = "user.id", target = "user")
    public abstract UserProjectIdResponseDto mapEntityToDtoIds(UserProject userProject);
}
