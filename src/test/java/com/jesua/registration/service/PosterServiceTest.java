package com.jesua.registration.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.jesua.registration.builder.FileBuilder;
import com.jesua.registration.config.AwsProperties;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.Project;
import com.jesua.registration.repository.PosterRepository;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.PosterBuilder.buildPoster;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PosterServiceTest {

    @InjectMocks
    PosterService posterService;

    @Mock
    AmazonS3 awsClient;

    @Mock
    PosterRepository posterRepository;

    @Mock
    private AwsProperties awsProperties;

    @Spy
    private FileBuilder fileBuilder;

    private String keyName;

    private ObjectMetadata getObjectMetadata(byte[] bytes, String type) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(type);
        objectMetadata.setContentLength(bytes.length);
        return objectMetadata;
    }

    @Disabled
    @Test
    void download() throws IOException {

        String fileName = "jesua_pixel.png";
        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);

        Project project = buildProject(1);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        byte[] bytes = fileBuilder.getBytesFromFile(fileName);

        ObjectMetadata objectMetadata = getObjectMetadata(bytes, MediaType.IMAGE_PNG_VALUE);

        keyName = project.getId() + "/" + contentId;
        awsClient.putObject("jesua", keyName, new ByteArrayInputStream(bytes), objectMetadata);

        S3Object s3Object = awsClient.getObject("jesua", keyName);

        doReturn(Optional.of(poster)).when(posterRepository).findByContentId(poster.getContentId());
        doReturn("jesua").when(awsProperties).getBucket();
//        when(awsProperties.getBucket()).thenReturn("jesua");
        doReturn(s3Object).when(awsClient).getObject("jesua", keyName);

        //run code
        byte[] download = posterService.download(contentId);

        //test
        verify(posterRepository).findByContentId(poster.getContentId());
//
//        assertThat(projectResponseDto).isNotNull();
//        assertThat(projectResponseDto).usingRecursiveComparison().isEqualTo(userProjectResponseDto);
    }

    @Disabled
    @Test
    void upload() {
    }
}