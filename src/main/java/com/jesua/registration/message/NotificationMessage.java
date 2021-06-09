package com.jesua.registration.message;

import com.jesua.registration.entity.Follower;
import org.springframework.stereotype.Component;

import static com.jesua.registration.util.AppUtil.instantToString;

@Component
public class NotificationMessage extends AbstractMessageBuilder {

    final String subject = "REMINDER";
    String body;

    @Override
    public Message buildMessage(Follower follower) {
        this.body = String.format("Ahoj %s, akcia '%s - %s' sa blíži. Tešíme sa na Teba.", follower.getName(), follower.getCourse().getDescription(), instantToString(follower.getCourse().getStartDate()));
        this.body += getMessageFooter(follower);
        return new Message(follower.getEmail(), this.subject, this.body);
    }
}
