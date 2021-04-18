package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowerDto {
    private String name;
    private String email;
    private long eventId;
    private boolean gdpr;
    private String deviceDetail;
}
