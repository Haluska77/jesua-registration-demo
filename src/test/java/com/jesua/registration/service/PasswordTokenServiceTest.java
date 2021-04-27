package com.jesua.registration.service;

import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.dto.TokenState;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.dto.UserTokenDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.PasswordTokenException;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.PasswordTokenRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.FollowerBuilder.TOKEN;
import static com.jesua.registration.builder.PasswordTokenBuilder.PASSWORD_TOKEN;
import static com.jesua.registration.builder.PasswordTokenBuilder.buildPasswordToken;
import static com.jesua.registration.builder.PasswordTokenBuilder.createPasswordDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
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

    private static User user;

    @BeforeAll
    static void setUp(){
        Project project = buildProject(1);
        user = buildUserWithId(USER_ID);
    }

    @Test
    void createAndSendTokenTest() {

        UserResponseDto userResponseDto = buildUserResponseDtoFromEntity(user);

        doReturn(Optional.of(user)).when(userRepository).findByEmailAndActiveTrue(user.getEmail());
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(any());

        UserResponseDto actualUserResponseDto = passwordTokenService.createAndSendTokenByUserEmail(user.getEmail());

        verify(userRepository).findByEmailAndActiveTrue(user.getEmail());
        verify(userMapper).mapEntityToDto(any());

        assertThat(actualUserResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);

    }

    @Test
    void createAndSendTokenThrowExceptionTest() {

        doReturn(Optional.empty()).when(userRepository).findByEmailAndActiveTrue(user.getEmail());

        assertThatThrownBy(() -> passwordTokenService.createAndSendTokenByUserEmail(user.getEmail()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not Found or not active");

    }

    @Test
    void savePasswordResetTokenTest() {

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
        doReturn(expectedPasswordToken).when(passwordTokenRepository).save(any());

        PasswordToken passwordToken = passwordTokenService.savePasswordResetToken(user, TOKEN);

        verify(passwordTokenRepository).save(any());

        assertThat(passwordToken).usingRecursiveComparison().isEqualTo(expectedPasswordToken);

    }

    @Test
    void passwordResetTokenIsSuccessTest() {

        UserTokenDto expectedUserTokenDto = new UserTokenDto(TOKEN, TokenState.SUCCESS);

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);

        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(any());

        UserTokenDto actualUserTokenDto = passwordTokenService.validatePasswordResetToken(TOKEN);

        verify(passwordTokenRepository).findByToken(any());
        assertThat(actualUserTokenDto).usingRecursiveComparison().isEqualTo(expectedUserTokenDto);

    }

    @Test
    void passwordResetTokenIsAppliedTest() {

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
        expectedPasswordToken.setApplied(true);
        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(any());

        assertThatThrownBy(() -> passwordTokenService.validatePasswordResetToken(TOKEN))
                .isInstanceOf(PasswordTokenException.class)
                .hasMessage("Token is applied, create new request !!!");

    }

    @Test
    void passwordResetTokenIsExpiredTest() {

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
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

        UserResponseDto userResponseDto = buildUserResponseDtoFromEntity(user);

        PasswordDto passwordDto = createPasswordDto("newPwd", PASSWORD_TOKEN);

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(PASSWORD_TOKEN);
        doReturn(Optional.of(user)).when(userRepository).findByPasswordTokens_Token(PASSWORD_TOKEN);
        doReturn(expectedPasswordToken).when(passwordTokenRepository).save(any());
        doReturn(userResponseDto).when(userMapper).mapEntityToDto(user);

        UserResponseDto actualUserResponseDto = passwordTokenService.changePassword(passwordDto);

        verify(passwordTokenRepository, times(2)).findByToken(PASSWORD_TOKEN);
        verify(userRepository).findByPasswordTokens_Token(PASSWORD_TOKEN);
        verify(passwordTokenRepository).save(any());
        verify(userMapper).mapEntityToDto(user);

        assertThat(actualUserResponseDto).usingRecursiveComparison().isEqualTo(userResponseDto);

    }

    @Test
    void changePasswordNotFoundTest() {

        PasswordDto passwordDto = createPasswordDto("newPwd", PASSWORD_TOKEN);

        PasswordToken expectedPasswordToken = buildPasswordToken(1, 10, user);
        doReturn(Optional.of(expectedPasswordToken)).when(passwordTokenRepository).findByToken(PASSWORD_TOKEN);
        doReturn(Optional.empty()).when(userRepository).findByPasswordTokens_Token(PASSWORD_TOKEN);

        assertThatThrownBy(() -> passwordTokenService.changePassword(passwordDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");

    }

    @Test
    void passwordTokenAppliedThrowExceptionTest() {

        doReturn(Optional.empty()).when(passwordTokenRepository).findByToken(any());

        assertThatThrownBy(() -> passwordTokenService.passwordTokenApplied(TOKEN))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Password Token not found");

    }
}