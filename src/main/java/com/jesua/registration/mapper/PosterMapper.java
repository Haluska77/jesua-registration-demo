package com.jesua.registration.mapper;

import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.entity.Poster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProjectMapper.class)
public abstract class PosterMapper {

    public abstract PosterResponseDto mapEntityToDto(Poster poster);
}
