package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.ProjectResponseDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.ProjectRole;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.exception.ErrorDTO;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDtoAndSavedProject;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProjectResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends BaseControllerTest {

    public static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    private List<ProjectResponseDto> projectList = new ArrayList<>();

    private List<UserProjectResponseDto> userProjectList = new ArrayList<>();

    @AfterEach
    public void tearDown() {
        if (!projectList.isEmpty()) {
            projectList.forEach(p -> projectRepository.deleteById(p.getId()));
        }
        if (!userProjectList.isEmpty()) {
            userProjectList.forEach(p -> {
                userProjectRepository.deleteAll();
                projectRepository.deleteById(p.getProject().getId());
                userRepository.deleteById(p.getUser().getId());
            });
        }
    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired ProjectRepository projectRepository){
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSuccessfulAllProjectsTest() throws Exception {

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
    @WithMockUser(roles = "ADMIN")
    void getSuccessfulProjectsFilteredByNameTest() throws Exception {

        Stream<Integer> integers = Stream.iterate(0, i -> i + 1);

        projectList = integers
                .limit(5)
                .map(f -> {
                    ProjectDto projectDto = buildProjectDto();
                    projectDto.setShortName(projectDto.getShortName() + f);
                    Project project = buildProjectFromDto(projectDto);
                    projectRepository.save(project);
                    return buildProjectResponseDtoFromEntity(project);
                })
                .collect(Collectors.toList());

        MockHttpServletResponse response = mockMvc
                .perform(get("/projects/")
                        .queryParam("name", "jesua2")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<ProjectResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
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

        User user = buildUserWithOutId();
        userRepository.save(user);

        UserProject userProject = buildUserProject(project, user);
        UserProjectResponseDto userProjectResponseDto = buildUserProjectResponseDto(userProject);

        MockHttpServletResponse response = mockMvc
                .perform(post("/projects/add/" + user.getId())
                        .content(objectMapper.writeValueAsString(projectDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserProjectResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        userProjectList.add(successResponse.getResponse().getBody());
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("created", "project.id", "project.created", "user.created").isEqualTo(userProjectResponseDto);
        assertThat(successResponse.getResponse().getBody().getProject().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getProject().getCreated()).isNotNull();
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void createProjectUnauthorizedTest() throws Exception {
        ProjectDto projectDto = buildProjectDto();

        String contentAsString = mockMvc
                .perform(post("/projects/add/1")
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

        Project updatedProject = buildProjectFromDtoAndSavedProject(project, updatedProjectDto);
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void mapUserToProjectTest() throws Exception {

        User user = buildUserWithOutId();
        userRepository.save(user);

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        MockHttpServletResponse response = mockMvc
                .perform(post("/projects/map/user/" + user.getId() + "/project/" + project.getId())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<String> successResponse = objectMapper.readValue(response.getContentAsString().getBytes(StandardCharsets.UTF_8), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse().getBody()).isNull();
        assertThat(successResponse.getResponse().getMessage()).isEqualTo("User has been successfully mapped to project");
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

        UserProjectId userProjectId = new UserProjectId();
        userProjectId.setUserId(user.getId());
        userProjectId.setProjectId(project.getId());

        runInDbTransaction(() -> {
                    UserProject userProject = userProjectRepository.findById(userProjectId).orElseGet(() -> fail("User Project not created"));

                    assertThat(userProject.getUser().getId()).isEqualTo(user.getId());
                    assertThat(userProject.getProject().getId()).isEqualTo(project.getId());
                    assertThat(userProject.getRole()).isEqualTo(ProjectRole.OWNER);
                    assertThat(userProject.getCreated()).isNotNull();
                }
        );
    }

    protected void runInDbTransaction(Runnable runnable) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@Nullable TransactionStatus status) {
                runnable.run();
            }
        });
    }
}