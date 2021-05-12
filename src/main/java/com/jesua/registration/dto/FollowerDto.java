package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FollowerDto {
    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private Long eventId;

    @NotNull
    private boolean gdpr;

    private String deviceDetail;
}
