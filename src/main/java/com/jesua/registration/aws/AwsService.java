package com.jesua.registration.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.jesua.registration.config.AwsProperties;
import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.filter.PosterFilter;
import com.jesua.registration.mapper.PosterMapper;
import com.jesua.registration.repository.PosterRepository;
import com.jesua.registration.repository.PosterSpecification;
import com.jesua.registration.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AwsService {

    private final AmazonS3 awsClient;
    private final AwsProperties awsProperties;
    private final PosterRepository posterRepository;
    private final ProjectService projectService;
    private final PosterMapper posterMapper;

    public byte[] download(long posterId) {

        Poster poster = posterRepository.findById(posterId).orElseThrow(() -> new NoSuchElementException("Poster not found !!!"));
        String keyName = poster.getProject().getId() + "/" + poster.getContentId();

        try {
            S3ObjectInputStream objectContent = awsClient.getObject(awsProperties.getBucket(), keyName).getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download image", e);
        }
    }

    public PosterResponseDto upload(long projectId, MultipartFile multipartFile) throws IOException {

        String origFileName = FilenameUtils.getName(multipartFile.getOriginalFilename());

        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(origFileName);
        uploadToS3(multipartFile, projectId + "/" + contentId);

        Poster poster = createPoster(projectId, origFileName, contentId);
        posterRepository.save(poster);

        return posterMapper.mapEntityToDto(poster);
    }

    private Poster createPoster(long projectId, String origFileName, String contentId) {
        Poster poster = new Poster();
        poster.setFileName(origFileName);
        poster.setProject(projectService.getProject(projectId));
        poster.setContentId(contentId);
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
}
