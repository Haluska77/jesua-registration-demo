package com.jesua.registration.builder;

import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PasswordTokenBuilder {

    public static final String PASSWORD_TOKEN = "sdf5213ER45132df";

    public static PasswordToken buildPasswordToken(long id, long expDelay, User user) {
        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setId(id);
        passwordToken.setToken(PASSWORD_TOKEN);
        passwordToken.setExpiration(Instant.now().plus(expDelay, ChronoUnit.MINUTES));
        passwordToken.setApplied(false);
        passwordToken.setUser(user);
        return passwordToken;
    }


    public static PasswordDto createPasswordDto(String password, String token) {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword(password);
        passwordDto.setConfirmNewPassword(password);
        passwordDto.setToken(token);
        return passwordDto;
    }
}
