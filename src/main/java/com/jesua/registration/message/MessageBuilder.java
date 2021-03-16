package com.jesua.registration.message;

import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.jesua.registration.util.AppUtil.instantToString;

@Component
public class MessageBuilder {

    private static final String CHANGE_PASSWORD_URL = "/password/register/";
    private static final String REGISTRATION_UNSUBSCRIBE_URL = "/registration/unsubscribe";
    private static final String EMAIL_SUBJECT = "Registrácia na kurz JEŠUA";

    @Value("${origin.url}")
    private String originUrl;
    @Value("${password.token.expiration}")
    private String pwdTokenExp;

    public Message buildSuccessMessage(Follower follower, Course course) {

        String body = String.format("Ahoj %s, úspešne si sa prihlásil na kurz Ješua '%s - %s'.", follower.getName(), course.getDescription(), instantToString(course.getStartDate()));
        body += getMessageFooter(follower);

        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildUnsuccessMessage(Follower follower, Course course) {

        String body = String.format("Ahoj %s, úspešne si sa prihlásil na kurz Ješua '%s - %s'. <br><br>" +
                "Momentálne je kapacita kurzu (" + course.getCapacity() + " ľudí) už naplnená.<br>" +
                "V prípade, že sa niektorý z účastníkov kurzu odhlási, dáme ti okamžite vedieť na tento email.", follower.getName(), course.getDescription(), instantToString(course.getStartDate()));
        body += getMessageFooter(follower);

        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildSubstituteMessage(Follower follower, Course course) {

        String body = String.format("Ahoj %s, niektorý z účastníkov kurzu JEŠUA '%s - %s' sa odhlásil. Radi Ťa uvidíme.", follower.getName(), course.getDescription(), instantToString(course.getStartDate()));
        body += getMessageFooter(follower);
        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildFailedMessage(Follower follower, Course course) {
        String body = String.format("Ahoj %s, tvoju registráciu na kurz JEŠUA '%s - %s' sa nepodarilo spracovať. " +
                "Prosím kontaktuj administrátora.", follower.getName(), course.getDescription(), instantToString(course.getStartDate()));

        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildNotificationMessage(Follower follower, Course course) {

        String body = String.format("Ahoj %s, kurz JEŠUA '%s - %s' sa blíži. Tešíme sa na Teba.", follower.getName(), course.getDescription(), instantToString(course.getStartDate()));
        body += getMessageFooter(follower);

        return new Message(follower.getEmail(), "REMINDER", body);
    }

    public Message buildResetPasswordMessage(User user, String token) {

        String body = String.format("Ahoj %s, na obnovenie hesla klikni na nasledujuci link<br><br>" +
                "<b><a href=\""+originUrl+ CHANGE_PASSWORD_URL + "%s\">" +originUrl+ CHANGE_PASSWORD_URL + "%s</a></b>.<br><br>" +
                "Link je platny "+pwdTokenExp+" minut.", user.getUserName(), token, token);
        return new Message(user.getEmail(), "PASSWORD RESET", body);
    }

    private String getMessageFooter(Follower follower) {
        String url = originUrl + REGISTRATION_UNSUBSCRIBE_URL;
        String MESSAGE_FOOTER = "<br><br>V prípade, že sa nebudeš môcť zúčastniť, prosím odhlás sa kliknutím na tento <b><a href=\"%s?token=%s&event=%s\">link</a></b><Br>" +
                "Uvoľníš tým miesto inému záujemcovi.<Br><br>Ďakujeme<br>Tím JEŠUA";
        return String.format(MESSAGE_FOOTER, url, follower.getToken(), follower.getCourse().getId());

    }
}
