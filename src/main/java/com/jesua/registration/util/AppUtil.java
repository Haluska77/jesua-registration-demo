package com.jesua.registration.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AppUtil {

    public static String generateToken() {
        String rawToken = UUID.randomUUID().toString();
        return rawToken.replace("-", "");

    }

    public static String instantToString(Instant instant) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm").withZone(ZoneId.of("Europe/Bratislava"));
        LocalDateTime utc = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Bratislava"));
        return utc.format(dateTimeFormatter);
    }

    public static Instant stringToInstant(String string) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").withZone(ZoneId.of("Europe/Bratislava"));
        return Instant.from(dateTimeFormatter.parse(string));
    }

}
