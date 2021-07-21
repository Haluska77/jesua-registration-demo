package com.jesua.registration.builder;

import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.dto.PosterResponseWithDataDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.Project;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;

public class PosterBuilder {

    public static Poster buildPoster(Project project, String origFileName, String contentId, String fileType) {
        Poster poster = new Poster();
        poster.setFileName(origFileName);
        poster.setProject(project);
        poster.setContentId(contentId);
        poster.setFileType(fileType);
        return poster;
    }

    public static PosterResponseDto buildPosterResponseDto(ProjectResponseDto project, String origFileName, String contentId, String fileType) {
        PosterResponseDto posterResponseDto = new PosterResponseDto();
        posterResponseDto.setFileName(origFileName);
        posterResponseDto.setProject(project);
        posterResponseDto.setContentId(contentId);
        posterResponseDto.setFileType(fileType);
        return posterResponseDto;
    }


    public static PosterResponseDto buildPosterResponseDtoFromEntity(Poster poster) {
        PosterResponseDto posterResponseDto = new PosterResponseDto();
        posterResponseDto.setId(poster.getId());
        posterResponseDto.setFileName(poster.getFileName());
        posterResponseDto.setProject(buildProjectResponseDtoFromEntity(poster.getProject()));
        posterResponseDto.setContentId(poster.getContentId());
        posterResponseDto.setFileType(poster.getFileType());
        posterResponseDto.setCreated(poster.getCreated());
        return posterResponseDto;
    }

    public static PosterResponseWithDataDto buildPosterResponseWithDataDtoFromEntity(Poster poster, byte[] fileData) {
        PosterResponseWithDataDto posterResponseDto = new PosterResponseWithDataDto();
        posterResponseDto.setId(poster.getId());
        posterResponseDto.setFileName(poster.getFileName());
        posterResponseDto.setProject(buildProjectResponseDtoFromEntity(poster.getProject()));
        posterResponseDto.setContentId(poster.getContentId());
        posterResponseDto.setFileType(poster.getFileType());
        posterResponseDto.setCreated(poster.getCreated());
        posterResponseDto.setFileData(fileData);
        return posterResponseDto;
    }

    public static PosterResponseWithDataDto buildPosterResponseWithDataDto(ProjectResponseDto project, String origFileName, String contentId, String fileType, byte[] fileData) {
        PosterResponseWithDataDto posterResponseDto = new PosterResponseWithDataDto();
        posterResponseDto.setFileName(origFileName);
        posterResponseDto.setProject(project);
        posterResponseDto.setContentId(contentId);
        posterResponseDto.setFileType(fileType);
        posterResponseDto.setFileData(fileData);
        return posterResponseDto;
    }
}
