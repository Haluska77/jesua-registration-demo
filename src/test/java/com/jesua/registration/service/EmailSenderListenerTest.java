package com.jesua.registration.service;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.User;
import com.jesua.registration.event.EmailSenderListener;
import com.jesua.registration.event.FollowerCreatedEvent;
import com.jesua.registration.event.PasswordTokenCreatedEvent;
import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.SubstituteMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildSavedCourse;
import static com.jesua.registration.builder.FollowerBuilder.buildFullFollower;
import static com.jesua.registration.builder.PasswordTokenBuilder.buildPasswordToken;
import static com.jesua.registration.builder.UserBuilder.buildUserWithId;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailSenderListenerTest {

    public static final UUID USER_ID = UUID.randomUUID();
    @Mock
    private PasswordTokenService passwordTokenService;

    @Mock
    private SubstituteMessage substituteMessage;

    @Mock
    private EmailServiceImpl emailService;

    @InjectMocks
    private EmailSenderListener emailSenderListener;


    @Test
    void onUserCreatedTest() throws MessagingException {
        // prepare
        User user = buildUserWithId(USER_ID);
        Course course = buildSavedCourse(1L, user, 50, null);
        Follower follower = buildFullFollower(USER_ID, "token", null, true, course);

        FollowerCreatedEvent followerCreatedEvent = new FollowerCreatedEvent(follower, substituteMessage.buildMessage(follower));

        // do work
        emailSenderListener.onUserCreated(followerCreatedEvent);

        // test
        verify(emailService).sendMessage(followerCreatedEvent.getEmailMessage());
    }


    @Test
    void onTokenCreatedTest() throws MessagingException {
        // prepare
        User user = buildUserWithId(USER_ID);
        PasswordToken passwordToken = buildPasswordToken(2L, 15L, user);

        PasswordTokenCreatedEvent passwordTokenCreatedEvent = new PasswordTokenCreatedEvent(passwordToken, passwordTokenService.buildResetPasswordMessage(user, "token"));

        // do work
        emailSenderListener.onTokenCreated(passwordTokenCreatedEvent);

        // test
        verify(emailService).sendMessage(passwordTokenCreatedEvent.getEmailMessage());
    }
}
