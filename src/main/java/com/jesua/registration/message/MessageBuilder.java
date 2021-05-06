package com.jesua.registration.message;

import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.jesua.registration.util.AppUtil.instantToString;

@Component
public class MessageBuilder {

    private static final String CHANGE_PASSWORD_URL = "/password/register/";
    private static final String REGISTRATION_UNSUBSCRIBE_URL = "/registration/unsubscribe";
    private static final String EMAIL_SUBJECT = "Registrácia";

    @Value("${origin.url}")
    private String originUrl;
    @Value("${password.token.expiration}")
    private String pwdTokenExp;

    public Message buildSuccessMessage(Follower follower) {

        String body = String.format("Ahoj %s, úspešne si sa prihlásil na '%s - %s'.", follower.getName(), follower.getCourse().getDescription(), instantToString(follower.getCourse().getStartDate()));
        body += getMessageFooter(follower);

        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildUnsuccessMessage(Follower follower) {

        String body = String.format("Ahoj %s, úspešne si sa prihlásil na '%s - %s'. <br><br>" +
                "Momentálne je kapacita akcie (" + follower.getCourse().getCapacity() + " ľudí) už naplnená.<br>" +
                "V prípade, že sa niektorý z účastníkov akcie odhlási, dáme ti okamžite vedieť na tento email.", follower.getName(), follower.getCourse().getDescription(), instantToString(follower.getCourse().getStartDate()));
        body += getMessageFooter(follower);

        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildSubstituteMessage(Follower follower) {

        String body = String.format("Ahoj %s, niektorý z účastníkov akcie '%s - %s' sa odhlásil. Radi Ťa uvidíme.", follower.getName(), follower.getCourse().getDescription(), instantToString(follower.getCourse().getStartDate()));
        body += getMessageFooter(follower);
        return new Message(follower.getEmail(), EMAIL_SUBJECT, body);
    }

    public Message buildNotificationMessage(Follower follower) {

        String body = String.format("Ahoj %s, akcia '%s - %s' sa blíži. Tešíme sa na Teba.", follower.getName(), follower.getCourse().getDescription(), instantToString(follower.getCourse().getStartDate()));
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
