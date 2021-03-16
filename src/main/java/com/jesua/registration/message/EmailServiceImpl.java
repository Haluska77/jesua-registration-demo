package com.jesua.registration.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender mailSender;

    public void sendMessage(Message message) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper email = new MimeMessageHelper(mimeMessage);

        try {
            email.setTo(message.getTo());
            email.setSubject(message.getSubject());
            email.setText(message.getText(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MessagingException(e.getMessage());
        }
    }

}
