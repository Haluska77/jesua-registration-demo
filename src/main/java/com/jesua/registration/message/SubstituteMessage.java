package com.jesua.registration.message;

import com.jesua.registration.entity.Follower;
import org.springframework.stereotype.Component;

import static com.jesua.registration.util.AppUtil.instantToString;

@Component
public class SubstituteMessage extends AbstractMessageBuilder {

    final String subject = "Registrácia";
    String body;

    @Override
    public Message buildMessage(Follower follower) {
        this.body = String.format("Ahoj %s, niektorý z účastníkov akcie '%s - %s' sa odhlásil. Radi Ťa uvidíme.", follower.getName(), follower.getCourse().getDescription(), instantToString(follower.getCourse().getStartDate()));
        this.body += getMessageFooter(follower);
        return new Message(follower.getEmail(), this.subject, this.body);
    }
}
