package com.jesua.registration.message;

import javax.mail.MessagingException;

public interface EmailService {

    void sendMessage(Message message) throws MessagingException;
}
