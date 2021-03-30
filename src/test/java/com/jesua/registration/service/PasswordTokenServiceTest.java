package com.jesua.registration.service;

import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.dto.TokenState;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.dto.UserTokenDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.PasswordTokenException;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.PasswordTokenRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.FollowerBuilder.TOKEN;
import static com.jesua.registration.builder.PasswordTokenBuilder.PASSWORD_TOKEN;
import static com.jesua.registration.builder.PasswordTokenBuilder.buildPasswordToken;
import static com.jesua.registration.builder.UserBuilder.buildUser;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PasswordTokenServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private PasswordTokenRepository passwordTokenRepository;

    @Mock
    private MessageBuilder messageBuilder;

    @Mock
    private PasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PasswordTokenService passwordTokenService;

    @Test
    void createAndSendTokenTest() {

        User user = buildUser(USER_ID);
        UserResponseDto userResponseDto = buildUserResponseDto(user);

        doReturn(Optional.of(user)).when(userRepository).findByEmailAndActiveTrue(user.getEmail());
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(any());

        UserResponseDto actualUserResponseDto = passwordTokenService.createAndSendTokenByUserEmail(user.getEmail());

        verify(userRepository).findByEmailAndActiveTrue(user.getEmail());
        verify(userMapper).mapEntityToDto(any());

        assertThat(actualUserResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);

    }

    @Test
    void savePasswordResetTokenTest() {
        User user = buildUser(USER_ID);

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
        doReturn(expectedPasswordToken).when(passwordTokenRepository).save(any());

        PasswordToken passwordToken = passwordTokenService.savePasswordResetToken(user, TOKEN);

        verify(passwordTokenRepository).save(any());

        assertThat(passwordToken).usingRecursiveComparison().isEqualTo(expectedPasswordToken);

    }

    @Test
    void passwordResetTokenIsSuccessTest() {

        UserTokenDto expectedUserTokenDto = new UserTokenDto(TOKEN, TokenState.SUCCESS);

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, buildUser(USER_ID));

        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(any());

        UserTokenDto actualUserTokenDto = passwordTokenService.validatePasswordResetToken(TOKEN);

        verify(passwordTokenRepository).findByToken(any());
        assertThat(actualUserTokenDto).usingRecursiveComparison().isEqualTo(expectedUserTokenDto);

    }

    @Test
    void passwordResetTokenIsAppliedTest() {

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, buildUser(USER_ID));
        expectedPasswordToken.setApplied(true);
        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(any());

        assertThatThrownBy(() -> passwordTokenService.validatePasswordResetToken(TOKEN))
                .isInstanceOf(PasswordTokenException.class)
                .hasMessage("Token is applied, create new request !!!");

    }

    @Test
    void passwordResetTokenIsExpiredTest() {

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, buildUser(USER_ID));
        expectedPasswordToken.setExpiration(Instant.now().minusSeconds(60));
        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(any());

        assertThatThrownBy(() -> passwordTokenService.validatePasswordResetToken(TOKEN))
                .isInstanceOf(PasswordTokenException.class)
                .hasMessage("Token has expired !!!");

    }

    @Test
    void passwordResetTokenIsInvalidTest() {

        doReturn(Optional.empty()).when(passwordTokenRepository).findByToken(any());

        assertThatThrownBy(() -> passwordTokenService.validatePasswordResetToken(TOKEN))
                .isInstanceOf(PasswordTokenException.class)
                .hasMessage("Token is invalid !!!");

    }

    @Test
    void changePasswordTest() {
        User user = buildUser(USER_ID);
        UserResponseDto userResponseDto = buildUserResponseDto(user);

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword("pwd");
        passwordDto.setConfirmNewPassword("pwd");
        passwordDto.setToken(PASSWORD_TOKEN);

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(any());
        doReturn(Optional.of(user)).when(userRepository).findByPasswordTokens_Token(any());
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(any());

        UserResponseDto actualUserResponseDto = passwordTokenService.changePassword(passwordDto);

        verify(passwordTokenRepository, times(2)).findByToken(any());
        verify(userRepository).findByPasswordTokens_Token(any());
        verify(userMapper).mapEntityToDto(any());

        assertThat(actualUserResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);

    }

}