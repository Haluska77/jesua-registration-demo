package com.jesua.registration.controller;

import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.dto.PosterResponseWithDataDto;
import com.jesua.registration.entity.filter.PosterFilter;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.service.PosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("poster/")
public class PosterController {

    private final PosterService posterService;

    @GetMapping("metaData")
    public List<PosterResponseDto> getFiles(PosterFilter posterFilter) {
        return posterService.getPostersBy(posterFilter);
    }

    @GetMapping("all")
    public List<PosterResponseWithDataDto> getFilesWithData(PosterFilter posterFilter) {
        return posterService.getPostersWithDataBy(posterFilter);
    }

    @GetMapping("{contentId}")
    public SuccessResponse<byte[]> getFile(@PathVariable("contentId") String contentId) {
        byte[] download = posterService.download(contentId);
        return new SuccessResponse<>(download, null);
    }

    @PostMapping(path = "upload")
    public PosterResponseDto uploadFile(@RequestParam("projectId") long projectId, @RequestParam("file") MultipartFile file) throws IOException {

            return posterService.upload(projectId, file);
    }

    @DeleteMapping(path = "delete/{contentId}")
    public SuccessResponse<String> removeFile(@PathVariable("contentId") String contentId){

        posterService.delete(contentId);
        return new SuccessResponse<>(null, String.format("Súbor %s bol úspešne zmazaný!!!", contentId));
    }
}