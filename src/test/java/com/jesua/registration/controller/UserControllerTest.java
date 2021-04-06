package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.dto.UserResponseDto;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.ErrorDTO;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.security.dto.LoginDto;
import com.jesua.registration.security.dto.LoginResponseDto;
import com.jesua.registration.security.jwt.JwtProvider;
import com.jesua.registration.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();
    public static final String AUTHENTICATION_IS_REQUIRED = "Full authentication is required to access this resource";
    public static final String USER_HAS_BEEN_SUCCESSFULLY_CHANGED = "User has been successfully changed!";
    public static final String NEW_USER_REGISTERED_SUCCESSFULLY = "New user registered successfully!";
    public static final String USER_HAS_BEEN_CHANGED = "User has been changed";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void usersSuccessTest() throws Exception {

        List<UserResponseDto> userList = Stream.generate(UUID::randomUUID)
                .limit(5)
                .map(f -> {
                    UserDto userDto = buildUserDto();
                    User user = buildUserFromDto(userDto);
                    userRepository.save(user);
                    return buildUserResponseDto(user);
                })
                .collect(Collectors.toList());

        MockHttpServletResponse response = mockMvc
                .perform(get("/users/")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<UserResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
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

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSuccessTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        userRepository.save(user);
        UserResponseDto userResponseDto = buildUserResponseDto(user);

        MockHttpServletResponse response = mockMvc
                .perform(post("/users/update/" + user.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
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
        User user = buildUserFromDto(userDto);

        String contentAsString = mockMvc
                .perform(post("/users/update/" + user.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void switchActiveUserAccountTest() throws Exception {
        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        userRepository.save(user);
        UserResponseDto userResponseDto = buildUserResponseDto(user);
        userResponseDto.setActive(false);

        MockHttpServletResponse response = mockMvc
                .perform(get("/users/makeActive/")
                        .queryParam("userId", user.getId().toString())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
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

        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        userRepository.save(user);

        String contentAsString = mockMvc
                .perform(get("/users/makeActive/")
                        .queryParam("userId", user.getId().toString())
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }

    @Test
    void signInTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        userRepository.save(user);
        UserResponseDto userResponseDto = buildUserResponseDto(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(userDto.getEmail());
        loginDto.setPassword(userDto.getPassword());

        LoginResponseDto loginResponseDto = new LoginResponseDto(userResponseDto.getId(), userResponseDto.getAvatar(),
                userResponseDto.getName(), userResponseDto.getEmail(), userResponseDto.getRole(), userResponseDto.getActive(),
                userResponseDto.getCreated(), "eyJhbGciOiJIUzUxMiJ9." +
                "eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpYXQiOjE2MTc3MjE5MTIsImV4cCI6MTYxNzczMDkxMn0." +
                "xI-wP_2_R3xQP4Xk3PmW2n5z9Q41ECT1CsT-gqZQ3rMKfFxIRU5qevc9_TGmU1GthN-EqClncNyeO5d8bhoGfQ");


        MockHttpServletResponse response = mockMvc
                .perform(post("/users/signin")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<LoginResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("created", "token").isEqualTo(loginResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(loginResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getToken()).startsWith("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpYXQiOjE2MTc3Mj");
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signUpUserSuccessTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        UserResponseDto userResponseDto = buildUserResponseDto(user);

        MockHttpServletResponse response = mockMvc
                .perform(post("/users/signup")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<UserResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison().ignoringFields("id", "created").isEqualTo(userResponseDto);
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(userResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isEqualTo(NEW_USER_REGISTERED_SUCCESSFULLY);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void signUpUserUnauthorizedTest() throws Exception {

        UserDto userDto = buildUserDto();

        String contentAsString = mockMvc
                .perform(post("/users/signup")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);
    }
}