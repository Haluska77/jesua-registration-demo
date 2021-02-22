package com.jesua.registration.dto;

import lombok.Getter;

@Getter
public class FollowerDto {
    private String name;
    private String email;
    private int eventId;
    private boolean gdpr;

}
