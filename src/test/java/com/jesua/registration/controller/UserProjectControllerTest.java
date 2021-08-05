package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.ProjectDetailDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserProjectIdResponseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.ProjectRole;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDtoWithoutId;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProjectId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProjectControllerTest extends BaseControllerTest {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private UserProjectRepository userProjectRepository;

    private static User user;
    private static Project project;
    private static Project project2;
    private static UserProject userProject2;
    private static UserProject userProject3;
    private static UserProject userProject4;

    @BeforeAll
    static void setUp(@Autowired UserRepository userRepository,
                      @Autowired ProjectRepository projectRepository,
                      @Autowired UserProjectRepository userProjectRepository){

        user = buildUserWithOutId();
        userRepository.save(user);

        UserDto customUserDto = buildUserDto("test", "test@gmail.com", "tester", "ROLE_MODERATOR", true, "some.jpg");
        User user2 = buildUserFromDtoWithoutId(customUserDto);
        userRepository.save(user2);

        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        ProjectDto projectDto2 = buildProjectDto("testProject", "Project TEST", true);
        project2 = buildProjectFromDto(projectDto2);
        projectRepository.save(project2);

        userProject2 = buildUserProject(project2, user);
        userProjectRepository.save(userProject2);

        userProject3 = buildUserProject(project, user2);
        userProjectRepository.save(userProject3);

        userProject4 = buildUserProject(project2, user2);
        userProjectRepository.save(userProject4);
    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired ProjectRepository projectRepository,
                        @Autowired UserProjectRepository userProjectRepository){

        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void getAllUserProjectsTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/userProjects/")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<ProjectDetailDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getLength()).isEqualTo(2);
        assertThat(successResponse.getResponse().getBody().get(0).getUsers().size()).isEqualTo(1);
        assertThat(successResponse.getResponse().getBody().get(1).getUsers().size()).isEqualTo(2);

    }

    @Test
    void getUserProjectsByProjectTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/userProjects/project/"+project2.getId())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<ProjectDetailDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
        assertThat(successResponse.getResponse().getBody().getUsers().size()).isEqualTo(2);

    }

    @Test
    void getUserProjectsIdListTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/userProjects/list")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        UserProjectIdResponseDto userProjectIdResponseDto2 = buildUserProjectId(userProject2);
        UserProjectIdResponseDto userProjectIdResponseDto3 = buildUserProjectId(userProject3);
        UserProjectIdResponseDto userProjectIdResponseDto4 = buildUserProjectId(userProject4);

        SuccessResponse<List<UserProjectIdResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getLength()).isEqualTo(3);
        assertThat(successResponse.getResponse().getBody()).usingElementComparatorIgnoringFields("created")
                .containsExactlyInAnyOrderElementsOf(List.of(userProjectIdResponseDto2, userProjectIdResponseDto3, userProjectIdResponseDto4));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void mapUserToProjectTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(post("/userProjects/map/user/" + user.getId() + "/project/" + project.getId())
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