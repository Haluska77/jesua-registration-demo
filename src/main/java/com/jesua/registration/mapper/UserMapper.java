package com.jesua.registration.mapper;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.User;
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

    @Mapping(source = "name", target = "userName")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    public abstract User mapDtoToEntity(UserDto userDto);

    @Mapping(source = "name", target = "userName")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    public abstract User mapDtoToEntity(UserDto userDto, @MappingTarget User user);

    @Named("encodePassword")
    String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Mapping(source = "userName", target = "name")
//    @Mapping(target = "projects", ignore = true)
    public abstract UserResponseBaseDto mapEntityToDto(User user);
}
