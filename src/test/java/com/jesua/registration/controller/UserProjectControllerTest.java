package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.ProjectRole;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.entity.UserProjectId;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProjectControllerTest extends BaseControllerTest {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    @AfterEach
    public void cleanUp(){
        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void getUserProjects() {
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