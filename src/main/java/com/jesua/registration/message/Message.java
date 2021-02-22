package com.jesua.registration.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Message {

    private String to;
    private String subject;
    private String text;

}
