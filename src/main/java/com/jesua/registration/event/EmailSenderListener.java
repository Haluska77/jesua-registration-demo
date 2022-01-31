package com.jesua.registration.event;

import com.jesua.registration.message.EmailServiceImpl;
import com.jesua.registration.message.Message;
import com.jesua.registration.service.ProcessingErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;

@Component
@RequiredArgsConstructor
public class EmailSenderListener {

    private final EmailServiceImpl emailService;
    private final ProcessingErrorService processingErrorService;

    @Async
    @TransactionalEventListener(FollowerCreatedEvent.class)
    public void onUserCreated(FollowerCreatedEvent followerCreated) {

        try {
            emailService.sendMessage(followerCreated.getEmailMessage());
        } catch (MessagingException e) {
            String errorMessage = "Registration for followerId: " + followerCreated.getSavedFollower().getId() + " was not sent to "
                    + followerCreated.getSavedFollower().getEmail() + ". " +
                    "Error: " + e.getMessage();
            processingErrorService.createAndSaveProcessingError(errorMessage);
        }
    }

    @Async
    @TransactionalEventListener(PasswordTokenCreatedEvent.class)
    public void onTokenCreated(PasswordTokenCreatedEvent tokenCreated) {

        try {
            emailService.sendMessage(tokenCreated.getEmailMessage());
        } catch (MessagingException e) {
            String errorMessage = "TokenId: " + tokenCreated.getToken().getId() + " was not sent to "
                    + tokenCreated.getEmailMessage().getTo() + ". " +
                    "Error: " + e.getMessage();
            processingErrorService.createAndSaveProcessingError(errorMessage);
        }
    }


    public void sendNotificationEmail(String courseDescription, Message notificationMessage) {

        try {
            emailService.sendMessage(notificationMessage);
        } catch (MessagingException e) {
            String errorMessage = "Course: " + courseDescription + " was not sent to "
                    + notificationMessage.getTo() + ". " +
                    "Error: " + e.getMessage();
            processingErrorService.createAndSaveProcessingError(errorMessage);
        }
    }
}
