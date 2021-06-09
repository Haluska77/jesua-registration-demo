package com.jesua.registration.message;

import com.jesua.registration.entity.Follower;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractMessageBuilder implements MessageBuilder {

    private static final String REGISTRATION_UNSUBSCRIBE_URL = "/registration/unsubscribe";

    @Value("${origin.url}")
    private String originUrl;

    public abstract Message buildMessage(Follower follower);

    String getMessageFooter(Follower follower) {
        String url = originUrl + REGISTRATION_UNSUBSCRIBE_URL;
        String MESSAGE_FOOTER = "<br><br>V prípade, že sa nebudeš môcť zúčastniť, prosím odhlás sa kliknutím na tento <b><a href=\"%s?token=%s\">link</a></b><Br>" +
                "Uvoľníš tým miesto inému záujemcovi.<Br><br>Ďakujeme<br>Tím JEŠUA";
        return String.format(MESSAGE_FOOTER, url, follower.getToken());

    }
}
