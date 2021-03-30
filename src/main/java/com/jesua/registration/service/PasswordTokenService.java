package com.jesua.registration.service;

import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.dto.TokenState;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.dto.UserTokenDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.User;
import com.jesua.registration.event.PasswordTokenCreatedEvent;
import com.jesua.registration.exception.PasswordTokenException;
import com.jesua.registration.mapper.UserMapper;
import com.jesua.registration.message.MessageBuilder;
import com.jesua.registration.repository.PasswordTokenRepository;
import com.jesua.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.jesua.registration.dto.TokenState.APPLIED;
import static com.jesua.registration.dto.TokenState.EXPIRED;
import static com.jesua.registration.dto.TokenState.INVALID;
import static com.jesua.registration.dto.TokenState.SUCCESS;
import static com.jesua.registration.util.AppUtil.generateToken;

@Service
@RequiredArgsConstructor
public class PasswordTokenService {
    private final PasswordTokenRepository passwordTokenRepository;
    private final MessageBuilder messageBuilder;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${password.token.expiration}")
    private int pwdTokenExp;

    @Transactional
    public UserResponseDto createAndSendTokenByUserEmail(String email) {

        User user = userRepository.findByEmailAndActiveTrue(email)
                .map(this::savePasswordTokenToActiveUser)
                .orElse(null);

        return userMapper.mapEntityToDto(user);
    }

    private User savePasswordTokenToActiveUser(User user) {
        String token = generateToken();
        PasswordToken savedToken = savePasswordResetToken(user, token);
        eventPublisher.publishEvent(new PasswordTokenCreatedEvent(savedToken, messageBuilder.buildResetPasswordMessage(user, token)));
        return user;
    }

    public PasswordToken savePasswordResetToken(User user, String token) {
        PasswordToken myToken = new PasswordToken();
        myToken.setUser(user);
        myToken.setToken(token);
        myToken.setExpiration(Instant.now().plus(pwdTokenExp, ChronoUnit.MINUTES));
        return passwordTokenRepository.save(myToken);
    }

    public UserTokenDto validatePasswordResetToken(String token) {

        TokenState tokenState = passwordTokenRepository.findByToken(token)
                .map(t -> t.isApplied() ? APPLIED : t.getExpiration().isAfter(Instant.now())
                        ? SUCCESS : EXPIRED)
                .orElse(INVALID);

        if (SUCCESS.equals(tokenState)){
            return new UserTokenDto(token, SUCCESS);
        } else{
            throw new PasswordTokenException(tokenState.getMessage());
        }
    }

    @Transactional
    public UserResponseDto changePassword(PasswordDto passwordDto) {
        //validate token once again just before changing password
        validatePasswordResetToken(passwordDto.getToken());

        return userRepository.findByPasswordTokens_Token(passwordDto.getToken())
                .map(u -> {
                    changeAndSavePassword(u, passwordDto.getNewPassword());
                    passwordTokenApplied(passwordDto.getToken());
                    return userMapper.mapEntityToDto(u);
                }).orElse(null);
    }

    private void changeAndSavePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    private PasswordToken passwordTokenApplied(String token) {

        return passwordTokenRepository.findByToken(token)
                .map(t -> {
                    t.setApplied(true);
                    return passwordTokenRepository.save(t);
                }).orElse(null);

    }

}
