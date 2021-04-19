package com.jesua.registration.mapper;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.repository.ProjectRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = ProjectMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public abstract class UserMapper {

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ProjectRepository projectRepository;

    @Mapping(source = "name", target = "userName")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "project", source = "projectId", qualifiedByName = "project")
    public abstract User mapDtoToEntity(UserDto userDto);

    @Mapping(source = "name", target = "userName")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "project", source = "projectId", qualifiedByName = "project")
    public abstract User mapDtoToEntity(UserDto userDto, @MappingTarget User user);

    @Named("encodePassword")
    String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Named("project")
    Project getProject(long id) {
        return projectRepository.getOne(id);
    }

    @Mapping(source = "userName", target = "name")
    public abstract UserResponseDto mapEntityToDto(User user);
}
