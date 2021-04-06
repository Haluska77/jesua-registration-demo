package com.jesua.registration.builder;

import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.UUID;

public class UserBuilder {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String AVATAR = "avatar.svg";
    public static final String EMAIL = "admin@admin.com";
    public static final String NAME = "admin";
    public static final String PASSWORD_ENCRYPTED = "$2a$10$j7ArNKwi0BP14F1MMGhiFOIHHvFh3z/Sp/ghWaRWPSrKjAsJ.nnxm";
    public static final String PASSWORD = "admin";

    public static User buildUser(UUID id) {
        User user = new User();
        user.setId(id);
        user.setUserName(NAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD_ENCRYPTED);
        user.setRole(ROLE_ADMIN);
        user.setActive(true);
        user.setCreated(Instant.now());
        user.setAvatar(AVATAR);

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
        user.setPassword(new BCryptPasswordEncoder(10).encode(userDto.getPassword()));

        return user;
    }

    // input user from UI
    public static UserDto buildUserDto() {

        UserDto userDto = new UserDto();
        userDto.setName(NAME);
        userDto.setEmail(EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRole(ROLE_ADMIN);
        userDto.setActive(true);
        userDto.setAvatar(AVATAR);

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
