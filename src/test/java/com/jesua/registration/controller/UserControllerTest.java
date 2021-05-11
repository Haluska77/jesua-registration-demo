package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserProjectResponseDto;
import com.jesua.registration.dto.UserResponseBaseDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.exception.ErrorDto;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.dto.LoginDto;
import com.jesua.registration.security.dto.LoginResponseDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDtoWithoutId;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseBaseDtoFromEntity;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProjectResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {

    public static final String USER_HAS_BEEN_SUCCESSFULLY_CHANGED = "User has been successfully changed!";
    public static final String NEW_USER_REGISTERED_SUCCESSFULLY = "New user registered successfully!";
    public static final String USER_HAS_BEEN_CHANGED = "User has been changed";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectRepository userProjectRepository;

    private static Project project;
    private static Set<Project> projects;

    @BeforeAll
    static void setUp(@Autowired ProjectRepository projectRepository,
                      @Autowired UserRepository userRepository) {
        userRepository.deleteAll();
        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);
        projects = Set.of(project);
    }
    
    @AfterEach
    public void tearDown() {
        userProjectRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @AfterAll
    static void cleanUp(@Autowired ProjectRepository projectRepository) {
        
        projectRepository.deleteAll();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void usersSuccessTest() throws Exception {

        List<UserResponseBaseDto> userList = Stream.generate(UUID::randomUUID)
                .limit(5)
                .map(f -> {
                    User user = buildUserWithOutId();
                    userRepository.save(user);
                    return buildUserResponseBaseDtoFromEntity(user);
                })
                .collect(Collectors.toList());

        MockHttpServletResponse response = mockMvc
                .perform(get("/users/")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<UserResponseBaseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created").isEqualTo(userList);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(userList.size());

    }

    @Test
    void usersUnauthorizedTest() throws Exception {

        String contentAsString = mockMvc
                .perform(get("/users/"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSuccessTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDtoWithoutId(userDto);
        userRepository.save(user);
        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);

        MockHttpServletResponse response = mockMvc
                .perform(post("/users/update/" + user.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseBaseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created").isEqualTo(userResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(userResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(USER_HAS_BEEN_SUCCESSFULLY_CHANGED);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void updateUnauthorizedTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDtoWithoutId(userDto);

        String contentAsString = mockMvc
                .perform(post("/users/update/" + user.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void switchActiveUserAccountTest() throws Exception {

        User user = buildUserWithOutId();
        userRepository.save(user);
        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);
        userResponseDto.setActive(false);

        MockHttpServletResponse response = mockMvc
                .perform(get("/users/makeActive/")
                        .queryParam("userId", user.getId().toString())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseBaseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created").isEqualTo(userResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(userResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(USER_HAS_BEEN_CHANGED);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void switchActiveUserAccountUnauthorizedTest() throws Exception {

        User user = buildUserWithOutId();
        userRepository.save(user);

        String contentAsString = mockMvc
                .perform(get("/users/makeActive/")
                        .queryParam("userId", user.getId().toString())
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }

    @Test
    void signInTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDtoWithoutId(userDto);
        userRepository.save(user);

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        UserProject userProject = buildUserProject(project, user);
        userProjectRepository.save(userProject);

        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);

        UserProjectResponseDto userProjectResponseDto = buildUserProjectResponseDto(userProject);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setId(userResponseDto.getId());
        loginResponseDto.setName(userResponseDto.getName());
        loginResponseDto.setEmail(userResponseDto.getEmail());
        loginResponseDto.setRole(userResponseDto.getRole());
        loginResponseDto.setAvatar(userResponseDto.getAvatar());
        loginResponseDto.setActive(userResponseDto.getActive());
        loginResponseDto.setCreated(userResponseDto.getCreated());
        loginResponseDto.setProjects(Set.of(userProjectResponseDto));
        loginResponseDto.setToken("eyJhbGciOiJIUzUxMiJ9." +
                "eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpYXQiOjE2MTc3MjE5MTIsImV4cCI6MTYxNzczMDkxMn0." +
                "xI-wP_2_R3xQP4Xk3PmW2n5z9Q41ECT1CsT-gqZQ3rMKfFxIRU5qevc9_TGmU1GthN-EqClncNyeO5d8bhoGfQ");

        //credentials for login
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(userDto.getEmail());
        loginDto.setPassword(userDto.getPassword());

        MockHttpServletResponse response = mockMvc
                .perform(post("/users/signin")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<LoginResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("created", "token", "projects").isEqualTo(loginResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(loginResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getToken()).startsWith("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpYXQiOjE2M");

        assertThat(successResponse.getResponse().getBody().getProjects()).isNotEmpty();

        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void signInBadCredentialsTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDtoWithoutId(userDto);
        userRepository.save(user);

        //invalid credentials for login
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(userDto.getEmail());
        loginDto.setPassword("password");

        String response = mockMvc
                .perform(post("/users/signin")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo("Meno alebo heslo sú neplatné!!!");

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserSuccessTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDtoWithoutId(userDto);
        UserResponseBaseDto userResponseDto = buildUserResponseBaseDtoFromEntity(user);

        MockHttpServletResponse response = mockMvc
                .perform(post("/users/signup")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseBaseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("id", "created").isEqualTo(userResponseDto);
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getCreated()).isNotNull();
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(NEW_USER_REGISTERED_SUCCESSFULLY);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void createUserUnauthorizedTest() throws Exception {

        UserDto userDto = buildUserDto();

        String contentAsString = mockMvc
                .perform(post("/users/signup")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }
}