package com.jesua.registration.mapper;

import com.jesua.registration.builder.FileBuilder;
import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.dto.PosterResponseWithDataDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.UUID;

import static com.jesua.registration.builder.PosterBuilder.buildPoster;
import static com.jesua.registration.builder.PosterBuilder.buildPosterResponseDtoFromEntity;
import static com.jesua.registration.builder.PosterBuilder.buildPosterResponseWithDataDtoFromEntity;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PosterMapperTest {

    @InjectMocks
    private PosterMapperImpl posterMapper;

    @Mock
    private ProjectMapperImpl projectMapper;

    @Spy
    FileBuilder fileBuilder;

    @Test
    void mapEntityToDto() {

        String fileName = "jesua_pixel.png";
        String contentId = UUID.randomUUID().toString();

        Project project = buildProject(1);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        PosterResponseDto expectedPosterResponseDto = buildPosterResponseDtoFromEntity(poster);
        ProjectResponseDto projectResponseDto = buildProjectResponseDtoFromEntity(project);

        doReturn(projectResponseDto).when(projectMapper).mapEntityToDto(project);

        PosterResponseDto posterResponseDto = posterMapper.mapEntityToDto(poster);

        assertThat(posterResponseDto).usingRecursiveComparison().isEqualTo(expectedPosterResponseDto);
    }

    @Test
    void testMapEntityToDto() throws IOException {

        String fileName = "jesua_pixel.png";
        String contentId = UUID.randomUUID().toString();

        Project project = buildProject(1);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);

        byte[] bytes = fileBuilder.getBytesFromFile(fileName);

        PosterResponseWithDataDto expectedPosterResponseDto = buildPosterResponseWithDataDtoFromEntity(poster, bytes);
        ProjectResponseDto projectResponseDto = buildProjectResponseDtoFromEntity(project);

        doReturn(projectResponseDto).when(projectMapper).mapEntityToDto(project);

        PosterResponseWithDataDto posterResponseDto = posterMapper.mapEntityToDto(poster, bytes);

        assertThat(posterResponseDto).usingRecursiveComparison().isEqualTo(expectedPosterResponseDto);
    }
}