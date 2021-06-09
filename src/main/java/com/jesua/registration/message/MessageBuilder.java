package com.jesua.registration.message;

import com.jesua.registration.entity.Follower;

public interface MessageBuilder {

    Message buildMessage(Follower follower);
}
