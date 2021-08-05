package com.jesua.registration.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.builder.FileBuilder;
import com.jesua.registration.dto.PosterResponseDto;
import com.jesua.registration.dto.PosterResponseWithDataDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Poster;
import com.jesua.registration.entity.Project;
import com.jesua.registration.exception.ErrorDto;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.PosterRepository;
import com.jesua.registration.repository.ProjectRepository;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.jesua.registration.builder.PosterBuilder.buildPoster;
import static com.jesua.registration.builder.PosterBuilder.buildPosterResponseDto;
import static com.jesua.registration.builder.PosterBuilder.buildPosterResponseWithDataDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PosterControllerTest extends BaseControllerTest {

    @Autowired
    private AmazonS3 awsClient;

    @Autowired
    private PosterRepository posterRepository;

    @Autowired
    private FileBuilder fileBuilder;

    private static Project project;
    private final List<Poster> posterList = new ArrayList<>();
    private final List<PosterResponseWithDataDto> posterResponseWithDataDtoList = new ArrayList<>();
    private String keyName;

    @BeforeAll
    static void setUp(@Autowired ProjectRepository projectRepository) {
        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);
    }

    @AfterEach
    void cleanUp() {
        if (keyName != null) {
            awsClient.deleteObject(new DeleteObjectRequest("jesua", keyName));
        }
        posterRepository.deleteAll();
    }

    @AfterAll
    static void tearDown(@Autowired ProjectRepository projectRepository,
                         @Autowired PosterRepository posterRepository) {
        posterRepository.deleteAll();
        projectRepository.deleteAll();

    }

    @Test
    void getFilesTest() throws Exception {

        String contentId = UUID.randomUUID().toString();
        Poster poster = buildPoster(project, "logo.jpg", contentId, MediaType.IMAGE_JPEG_VALUE);
        posterRepository.save(poster);
        posterList.add(poster);

        Poster poster2 = buildPoster(project, "logo2.jpg", UUID.randomUUID().toString(), MediaType.IMAGE_JPEG_VALUE);
        posterRepository.save(poster2);
        posterList.add(poster2);

        MockHttpServletResponse response = mockMvc
                .perform(get("/poster/metaData?projectId=" + project.getId())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<PosterResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created", "project.created").isEqualTo(posterList);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(posterList.size());
    }

    @Test
    void getFilesWithDataTest() throws Exception {

        String fileName = "jesua_pixel.png";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(fileName);
        byte[] bytes = fileBuilder.getBytesFromFile(fileName);

        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        posterRepository.save(poster);

        ObjectMetadata objectMetadata = getObjectMetadata(bytes, MediaType.IMAGE_PNG_VALUE);

        keyName = project.getId() + "/" + contentId;
        awsClient.putObject("jesua", keyName, resourceAsStream, objectMetadata);

        PosterResponseWithDataDto expectedResponse = buildPosterResponseWithDataDto(null, fileName, contentId,
                MediaType.IMAGE_PNG_VALUE, bytes);

        posterResponseWithDataDtoList.add(expectedResponse);

        MockHttpServletResponse response = mockMvc
                .perform(get("/poster/all?projectId=" + project.getId())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<PosterResponseWithDataDto>> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("id", "created", "project").isEqualTo(posterResponseWithDataDtoList);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(posterResponseWithDataDtoList.size());
    }

    @Test
    void getFileTest() throws Exception {

        String fileName = "jesua_pixel.png";
        byte[] bytes = fileBuilder.getBytesFromFile(fileName);

        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        posterRepository.save(poster);

        ObjectMetadata objectMetadata = getObjectMetadata(bytes, MediaType.IMAGE_PNG_VALUE);

        keyName = project.getId() + "/" + contentId;
        awsClient.putObject("jesua", keyName, new ByteArrayInputStream(bytes), objectMetadata);

        MockHttpServletResponse response = mockMvc
                .perform(get("/poster/" + contentId)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();


        SuccessResponse<byte[]> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isEqualTo(bytes);
    }

    @Test
    void getNotFoundFileTest() throws Exception {

        String fileName = "jesua_pixel.png";

        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        posterRepository.save(poster);

        String response = mockMvc
                .perform(get("/poster/" + contentId)
                )
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo("Failed to download image");

    }

    private ObjectMetadata getObjectMetadata(byte[] bytes, String type) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(type);
        objectMetadata.setContentLength(bytes.length);

        return objectMetadata;
    }

    @Test
    void uploadFileTest() throws Exception {

        String fileName = "logo_jesua2.png";
        byte[] bytes = fileBuilder.getBytesFromFile(fileName);
        MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.IMAGE_PNG_VALUE, bytes);

        PosterResponseDto expectedResponse = buildPosterResponseDto(null, fileName, UUID.randomUUID().toString(), MediaType.IMAGE_PNG_VALUE);
        MockHttpServletResponse response = mockMvc
                .perform(
                        multipart("/poster/upload")
                                .file(file)
                                .param("projectId", String.valueOf(project.getId()))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<PosterResponseDto> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getContentId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getProject().getId()).isEqualTo(project.getId());
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("id", "contentId", "created", "project").isEqualTo(expectedResponse);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

        keyName = project.getId() + "/" + successResponse.getResponse().getBody().getContentId();

    }

    @Test
    void deleteFileTest() throws Exception {

        String fileName = "jesua_pixel.png";
        byte[] bytes = fileBuilder.getBytesFromFile(fileName);

        String contentId = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);
        Poster poster = buildPoster(project, fileName, contentId, MediaType.IMAGE_PNG_VALUE);
        posterRepository.save(poster);

        ObjectMetadata objectMetadata = getObjectMetadata(bytes, MediaType.IMAGE_PNG_VALUE);

        keyName = project.getId() + "/" + contentId;
        awsClient.putObject("jesua", keyName, new ByteArrayInputStream(bytes), objectMetadata);

        MockHttpServletResponse response = mockMvc
                .perform(delete("/poster/delete/" + contentId)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();


        SuccessResponse<String> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNull();
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(String.format("Súbor %s bol úspešne zmazaný!!!", contentId));
    }
}