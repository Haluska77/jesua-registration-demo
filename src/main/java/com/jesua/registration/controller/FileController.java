package com.jesua.registration.controller;

import com.jesua.registration.aws.AwsService;
import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.entity.filter.PosterFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("file/")
public class FileController {

    private final AwsService awsService;

    @GetMapping("")
    public List<PosterResponseDto> getFiles(PosterFilter posterFilter) {
        return awsService.getPostersBy(posterFilter);
    }

    @PostMapping(path = "upload")
    public PosterResponseDto uploadFile(@RequestParam("projectId") long projectId, @RequestParam("file") MultipartFile file) throws IOException {

            return awsService.upload(projectId, file);
    }

}