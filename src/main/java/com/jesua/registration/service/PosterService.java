package com.jesua.registration.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.jesua.registration.config.AwsProperties;
import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.dto.PosterResponseWithDataDto;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.filter.PosterFilter;
import com.jesua.registration.mapper.PosterMapper;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.PosterRepository;
import com.jesua.registration.repository.PosterSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PosterService {

    private final AmazonS3 awsClient;
    private final AwsProperties awsProperties;
    private final PosterRepository posterRepository;
    private final ProjectService projectService;
    private final PosterMapper posterMapper;
    private final CourseRepository courseRepository;
    private final AwsService awsService;

    public byte[] download(String contentId) {

        String keyName = getKeyNameFromContentId(contentId);

        return awsService.getBytes(keyName);
    }


    private String getKeyNameFromContentId(String contentId) {
        Poster poster = posterRepository.findByContentId(contentId).orElseThrow(() -> new NoSuchElementException("Poster not found !!!"));
        return poster.getProject().getId() + "/" + poster.getContentId();
    }

    public PosterResponseDto upload(long projectId, MultipartFile multipartFile) throws IOException {

        String origFileName = FilenameUtils.getName(multipartFile.getOriginalFilename());
        String fileType = multipartFile.getContentType();

        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(origFileName);
        uploadToS3(multipartFile, projectId + "/" + contentId);

        Poster poster = createPoster(projectId, origFileName, contentId, fileType);
        posterRepository.save(poster);

        return posterMapper.mapEntityToDto(poster);
    }

    private Poster createPoster(long projectId, String origFileName, String contentId, String fileType) {
        Poster poster = new Poster();
        poster.setFileName(origFileName);
        poster.setProject(projectService.getProject(projectId));
        poster.setContentId(contentId);
        poster.setFileType(fileType);
        return poster;
    }

    private void uploadToS3(MultipartFile multipartFile, String fileName) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();
        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);
        awsClient.putObject(awsProperties.getBucket(), fileName, inputStream, objectMetadata);
    }

    private ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }

    public List<PosterResponseDto> getPostersBy(PosterFilter posterFilter) {
        return posterRepository.findAll(new PosterSpecification(posterFilter))
                .stream().map(posterMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public List<PosterResponseWithDataDto> getPostersWithDataBy(PosterFilter posterFilter) {

        return posterRepository.findAll(new PosterSpecification(posterFilter))
                .stream()
                .map(poster -> {
                    byte[] fileData = download(poster.getContentId());
                    return posterMapper.mapEntityToDto(poster, fileData);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(String contentId) {
        String keyName = getKeyNameFromContentId(contentId);
        removeFromS3(keyName);
        updateCourseImageHavingContentIdByNull(contentId);
        posterRepository.deleteByContentId(contentId);

    }

    private void removeFromS3(String keyName) {
        awsClient.deleteObject(new DeleteObjectRequest(awsProperties.getBucket(), keyName));
    }

    private void updateCourseImageHavingContentIdByNull(String contentId) {
        courseRepository.findByImage(contentId).forEach(course -> {
            course.setImage(null);
            courseRepository.save(course);
        });
    }
}
