package com.jesua.registration.security.dto;

import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class LoginResponseDto extends UserResponseBaseDto {

    private String token;
    private Set<UserProjectResponseDto> projects;

}
