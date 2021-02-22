package com.jesua.registration.event;

import com.jesua.registration.message.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;

@Component
@RequiredArgsConstructor
public class EmailSenderListener {

    private final EmailServiceImpl emailService;

    @TransactionalEventListener(FollowerCreatedEvent.class)
    public void onUserCreated(FollowerCreatedEvent followerCreated) throws MessagingException {

        //send user email
        try{
            emailService.sendMessage(followerCreated.getEmailMessage());
        } catch (MessagingException e) {
            throw new MessagingException(e.getMessage());
        }

    }

    @TransactionalEventListener(PasswordTokenCreatedEvent.class)
    public void onTokenCreated(PasswordTokenCreatedEvent tokenCreated) throws MessagingException {

        //send user email
        try{
            emailService.sendMessage(tokenCreated.getEmailMessage());
        } catch (MessagingException e) {
            throw new MessagingException(e.getMessage());
        }
    }
}
