package com.jesua.registration.event;

import com.jesua.registration.entity.Follower;
import com.jesua.registration.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowerCreatedEvent {

    private Follower savedFollower;
    private Message emailMessage;

}
