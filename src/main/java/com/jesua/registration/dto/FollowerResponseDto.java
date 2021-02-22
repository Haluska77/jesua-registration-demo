package com.jesua.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FollowerResponseDto {

    private FollowerResponse follower;
    private String message;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FollowerResponse {
        private final UUID id;
        private final boolean accepted;

    }

}
