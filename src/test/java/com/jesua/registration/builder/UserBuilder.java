package com.jesua.registration.builder;

import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;

import java.time.Instant;
import java.util.UUID;

public class UserBuilder {

    public static User buildUser(){
        User user = new User();
//        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setUserName("name");
        user.setAvatar("avatar.svg");
        user.setRole("ROLE_ADMIN");
        user.setActive(true);
        user.setCreated(Instant.now());
        user.setPassword("$2a$10$j7ArNKwi0BP14F1MMGhiFOIHHvFh3z/Sp/ghWaRWPSrKjAsJ.nnxm");

        return user;
    }

    public static UserResponseDto buildUserResponseDto(){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("test@test.com");
        userResponseDto.setName("name");
        userResponseDto.setAvatar("avatar.svg");
        userResponseDto.setRole("ROLE_ADMIN");
        userResponseDto.setActive(true);
        userResponseDto.setCreated(Instant.now());

        return userResponseDto;
    }
}
