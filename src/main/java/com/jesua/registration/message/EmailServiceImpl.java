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
//        Properties properties = System.getProperties();
//
//        // Setup mail server
////        properties.setProperty("mail.smtp.proxy.host", "localhost");
////        properties.setProperty("mail.smtp.proxy.port", "3128");
//
//        Address address = null;
//        try {
//            address = new InternetAddress(message.getTo());
//        } catch (AddressException e) {
//            throw new AddressException(e.getMessage());
//        }
//        //Get default session object
//        Session session = Session.getDefaultInstance(properties);
//        MimeMessage mMessage = new MimeMessage(session);
//        try {
//            mMessage.addRecipient(javax.mail.Message.RecipientType.TO, address);
//            mMessage.setSubject(message.getSubject());
//            mMessage.setText(message.getText());
//            Transport.send(mMessage);
//            log.info("Email successfully sent");
//        } catch (MessagingException e) {
//            throw new MessagingException(e.getMessage());
//        }


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
