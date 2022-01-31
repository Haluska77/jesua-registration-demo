package com.jesua.registration.event;

import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.message.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CourseNotificationCreatedEvent {

    private final CourseDto course;
    private final Message emailMessage;

}
