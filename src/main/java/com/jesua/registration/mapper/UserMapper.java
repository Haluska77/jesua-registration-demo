package com.jesua.registration.mapper;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
@Component
public abstract class UserMapper {

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @Mapping(source = "name", target = "userName")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "created", expression  = "java(Instant.now())")
    public abstract User mapDtoToEntity(UserDto userDto);

    @Mapping(source = "name", target = "userName")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    public abstract User mapDtoToEntity(UserDto userDto, @MappingTarget User user);

    @Named("encodePassword")
    String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Mapping(source = "userName", target = "name")
    public abstract UserResponseDto mapEntityToDto(User user);
}
