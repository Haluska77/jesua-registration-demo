package com.jesua.registration.dto;

import com.jesua.registration.oauth.AuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseBaseDto {

    private UUID id;
    private String avatar;
    private String name;
    private String email;
    private String role;
    private Boolean active;
    private Instant created;
    private AuthProvider authProvider;

}
