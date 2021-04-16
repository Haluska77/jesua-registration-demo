package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.PasswordDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.dto.UserTokenDto;
import com.jesua.registration.entity.PasswordToken;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.PasswordTokenRepository;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.jesua.registration.builder.PasswordTokenBuilder.createPasswordDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static com.jesua.registration.dto.TokenState.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordTokenControllerTest extends BaseControllerTest {

    public static final String LINK_NA_ZMENU_HESLA = "Na uvedený email bol poslaný link na zmenu hesla!";
    public static final String PASSWORD_HAS_BEEN_SUCCESSFULLY_CHANGED = "Password has been successfully changed !!!";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    private static User user;

    @BeforeAll
    static void createUser(@Autowired UserRepository userRepository,
                           @Autowired ProjectRepository projectRepository){

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        user = buildUserWithOutId(project);
        userRepository.save(user);

    }

    @AfterEach
    public void tearDown() {
        passwordTokenRepository.deleteAll();
    }

    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository,
                         @Autowired ProjectRepository projectRepository) {
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void userAccountTest() throws Exception {

        UserResponseDto userResponseDto = buildUserResponseDtoFromEntity(user);

        MockHttpServletResponse response = mockMvc
                .perform(get("/password/userAccount/" + user.getEmail())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created", "project.created").isEqualTo(userResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(userResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getProject().getCreated()).isCloseTo(userResponseDto.getProject().getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(LINK_NA_ZMENU_HESLA);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void validateTokenTest() throws Exception {

        PasswordToken passwordToken = savePasswordToken(user);

        MockHttpServletResponse response = mockMvc
                .perform(get("/password/validateToken/" + passwordToken.getToken())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserTokenDto> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getToken()).isEqualTo(passwordToken.getToken());
        assertThat(successResponse.getResponse().getBody().getTokenState()).isEqualTo(SUCCESS);
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void changePassword() throws Exception {

        PasswordToken passwordToken = savePasswordToken(user);
        PasswordDto passwordDto = createPasswordDto("newPwd", passwordToken.getToken());

        MockHttpServletResponse response = mockMvc
                .perform(post("/password/changePassword")
                        .content(objectMapper.writeValueAsString(passwordDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(PASSWORD_HAS_BEEN_SUCCESSFULLY_CHANGED);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    private PasswordToken savePasswordToken(User user) {
        PasswordToken passwordToken = new PasswordToken();
        passwordToken.setToken("sdf126SDDf213sdSDF26tryt6y51");
        passwordToken.setUser(user);
        passwordToken.setApplied(false);
        passwordToken.setExpiration(Instant.now().plusSeconds(60));
        passwordTokenRepository.save(passwordToken);
        return passwordToken;
    }
}