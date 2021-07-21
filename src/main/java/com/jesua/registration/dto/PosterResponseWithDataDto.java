package com.jesua.registration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PosterResponseWithDataDto extends PosterResponseDto {

    private byte[] fileData;
}
