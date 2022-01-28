package com.jesua.registration.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.jesua.registration.builder.FileBuilder;
import com.jesua.registration.config.AwsConfig;
import com.jesua.registration.config.AwsProperties;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.Project;
import com.jesua.registration.repository.PosterRepository;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.jesua.registration.builder.PosterBuilder.buildPoster;
import static com.jesua.registration.builder.ProjectBuilder.buildProject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties(value = AwsProperties.class)
class PosterServiceTest {

    @InjectMocks
    PosterService posterService;

    @Mock
    AmazonS3 awsClient;

    @Mock
    PosterRepository posterRepository;

    @Mock
    private AwsProperties awsProperties;

    @Mock
    private AwsService awsService;

    @Spy
    private FileBuilder fileBuilder;

    private String keyName;

    private ObjectMetadata getObjectMetadata(byte[] bytes, String type) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(type);
        objectMetadata.setContentLength(bytes.length);
        return objectMetadata;
    }

    @Test
    void download() throws IOException {
        AmazonS3 client = Mockito.mock(AmazonS3.class);
        String fileName = "jesua_pixel.png";
        String bucketName = "jesua";
        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);

        Project project = buildProject(1);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        byte[] bytes = fileBuilder.getBytesFromFile(fileName);

        ObjectMetadata objectMetadata = getObjectMetadata(bytes, MediaType.IMAGE_PNG_VALUE);

        keyName = project.getId() + "/" + contentId;
//        awsClient.putObject("jesua", keyName, new ByteArrayInputStream(bytes), objectMetadata);
        when(awsClient.getObjectMetadata(eq(bucketName), eq(keyName))).thenReturn(objectMetadata);

        when(awsClient.putObject(eq(new PutObjectRequest(bucketName, keyName,
                new ByteArrayInputStream(bytes), objectMetadata).withMetadata(objectMetadata)))).thenThrow(new AmazonServiceException("Amazon exception"));

        S3Object s3Object = awsClient.getObject(bucketName, keyName);
        doReturn(Optional.of(poster)).when(posterRepository).findByContentId(poster.getContentId());
        doReturn(bucketName).when(awsProperties).getBucket();
//        doReturn(s3Object).when(awsClient).getObject(bucketName, keyName);

        //run code
        byte[] download = posterService.download(contentId);

        //test
        verify(posterRepository).findByContentId(poster.getContentId());

        assertThat(download).isNotNull();
        assertThat(download.length).isGreaterThan(0);
        assertThat(download).isEqualTo(bytes);
    }

    @Disabled
    @Test
    void upload() {
    }
}