package com.jesua.registration.event;

import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.message.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PasswordTokenCreatedEvent {

    private final PasswordToken token;
    private final Message emailMessage;

}
