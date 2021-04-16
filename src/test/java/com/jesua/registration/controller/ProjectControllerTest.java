package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.exception.ErrorDTO;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends BaseControllerTest {

    @Autowired
    private ProjectRepository projectRepository;

    private List<ProjectResponseDto> projectList = new ArrayList<>();

    @AfterEach
    public void tearDown() {
        if (!projectList.isEmpty()) {
            projectList.forEach(p -> projectRepository.deleteById(p.getId()));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSuccessfulProjectsTest() throws Exception {

        projectList = Stream.generate(UUID::randomUUID)
                .limit(5)
                .map(f -> {
                    ProjectDto projectDto = buildProjectDto();
                    Project project = buildProjectFromDto(projectDto);
                    projectRepository.save(project);
                    return buildProjectResponseDtoFromEntity(project);
                })
                .collect(Collectors.toList());

        MockHttpServletResponse response = mockMvc
                .perform(get("/projects/")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<ProjectResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created").isEqualTo(projectList);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(projectList.size());
    }

    @Test
    void getUnauthorizedProjectsTest() throws Exception {

        String contentAsString = mockMvc
                .perform(get("/projects/"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createProjectSuccessTest() throws Exception {

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        ProjectResponseDto expectedProjectResponseDto = buildProjectResponseDtoFromEntity(project);

        MockHttpServletResponse response = mockMvc
                .perform(post("/projects/add")
                        .content(objectMapper.writeValueAsString(projectDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<ProjectResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("id", "created").isEqualTo(expectedProjectResponseDto);
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        projectList.add(successResponse.getResponse().getBody());
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(expectedProjectResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void createProjectUnauthorizedTest() throws Exception {
        ProjectDto projectDto = buildProjectDto();

        String contentAsString = mockMvc
                .perform(post("/projects/add")
                        .content(objectMapper.writeValueAsString(projectDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProjectSuccessTest() throws Exception {

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        ProjectDto updatedProjectDto = buildProjectDto();
        updatedProjectDto.setShortName("updated name");
        updatedProjectDto.setDescription("updated Description");
        updatedProjectDto.setActive(false);

        Project updatedProject = buildProjectFromDto(project, updatedProjectDto);
        ProjectResponseDto expectedProjectResponseDto = buildProjectResponseDtoFromEntity(updatedProject);

        MockHttpServletResponse response = mockMvc
                .perform(post("/projects/update/" + project.getId())
                        .content(objectMapper.writeValueAsString(updatedProjectDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<ProjectResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        projectList.add(successResponse.getResponse().getBody());
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created").isEqualTo(expectedProjectResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(expectedProjectResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void updateProjectUnauthorizedTest() throws Exception {
        ProjectDto projectDto = buildProjectDto();

        String contentAsString = mockMvc
                .perform(post("/projects/update/1")
                        .content(objectMapper.writeValueAsString(projectDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }

}