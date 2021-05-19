package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class FollowerDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @NotNull
    @Size(max = 100)
    private String email;

    @NotNull
    private Long eventId;

    @NotNull
    private boolean gdpr;

    private String deviceDetail;
}
