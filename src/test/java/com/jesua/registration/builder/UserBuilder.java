package com.jesua.registration.builder;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;

import java.time.Instant;
import java.util.UUID;

public class UserBuilder {

    public static User buildUser(UUID id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test@test.com");
        user.setUserName("name");
        user.setAvatar("avatar.svg");
        user.setRole("ROLE_ADMIN");
        user.setActive(true);
        user.setCreated(Instant.now());
        user.setPassword("$2a$10$j7ArNKwi0BP14F1MMGhiFOIHHvFh3z/Sp/ghWaRWPSrKjAsJ.nnxm");

        return user;
    }

    public static User buildUserFromDto(UserDto userDto) {

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUserName(userDto.getName());
        user.setAvatar(userDto.getAvatar());
        user.setRole(userDto.getRole());
        user.setActive(userDto.getActive());
        user.setCreated(Instant.now());
        user.setPassword(userDto.getPassword());

        return user;
    }

    // input user from UI
    public static UserDto buildUserDto() {

        UserDto userDto = new UserDto();
        userDto.setName("admin");
        userDto.setEmail("admin@admin.com");
        userDto.setPassword("$2a$10$j7ArNKwi0BP14F1MMGhiFOIHHvFh3z/Sp/ghWaRWPSrKjAsJ.nnxm");
        userDto.setRole("ROLE_ADMIN");
        userDto.setActive(true);
        userDto.setAvatar("avatar.svg");

        return userDto;
    }

    // output user submitted to UI
    public static UserResponseDto buildUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getUserName());
        userResponseDto.setAvatar(user.getAvatar());
        userResponseDto.setRole(user.getRole());
        userResponseDto.setActive(user.getActive());
        userResponseDto.setCreated(user.getCreated());

        return userResponseDto;
    }
}
