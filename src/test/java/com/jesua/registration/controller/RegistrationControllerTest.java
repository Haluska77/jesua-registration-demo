package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.FollowerResponseDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.ErrorDTO;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.FollowerRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static com.jesua.registration.util.AppUtil.instantToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {

    public final String AUTHENTICATION_IS_REQUIRED = "Full authentication is required to access this resource";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    FollowerRepository followerRepository;

    private static User user;
    private static Course course;
    private static Follower follower2;
    private UUID createdFollowerId;

    @BeforeAll
    static void createUser(@Autowired UserRepository userRepository,
                           @Autowired CourseRepository courseRepository,
                           @Autowired FollowerRepository followerRepository){
        UserDto userDto = buildUserDto();
        user = buildUserFromDto(userDto);
        userRepository.save(user);

        CourseDto courseDto = buildCourseDto(user.getId());
        course = buildCourseFromDto(courseDto);
        courseRepository.save(course);

        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower follower1 = buildFollowerFromDto(followerDto, course);
        followerRepository.save(follower1);

        follower2 = buildFollowerFromDto(followerDto, course);
        follower2.setToken("dd6fg513DFS5d12df3DFd52");
        followerRepository.save(follower2);
    }

    @AfterEach
    public void tearDown() {
        if(createdFollowerId != null) {
            followerRepository.deleteById(createdFollowerId);
        }

    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired CourseRepository courseRepository,
                        @Autowired FollowerRepository followerRepository){

        followerRepository.deleteAll();
        courseRepository.deleteById(course.getId());
        userRepository.deleteById(user.getId());
    }

    @Test
    void addFollowerSuccessfulTest() throws Exception {

        String responseMessage = "Vaša registrácia na kurz Ješua (" + course.getDescription() + ", " + instantToString(course.getStartDate()) + ") prebehla úspešne! ";

        FollowerDto followerDto1 = buildFollowerDto(course.getId());

        MockHttpServletResponse response = mockMvc
                .perform(post("/registration/add")
                        .content(objectMapper.writeValueAsString(followerDto1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<FollowerResponseDto.FollowerResponse> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {});

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        createdFollowerId = successResponse.getResponse().getBody().getId();
        assertThat(successResponse.getResponse().getBody().isAccepted()).isTrue();
        assertThat(successResponse.getResponse().getMessage()).startsWith(responseMessage);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSuccessfulFollowersTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/registration/"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<FollowerEntityResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(2);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(2);

    }

    @Test
    void getUnauthorizedFollowersTest() throws Exception {

        String contentAsString = mockMvc
                .perform(get("/registration/"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }

    @Test
    void unsubscribeFollowerSuccessfulTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/registration/unsubscribe")
                        .queryParam("token", follower2.getToken())
                        .queryParam("event", String.valueOf(follower2.getCourse().getId()))
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<FollowerResponseDto.FollowerResponse> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getId()).isEqualTo(follower2.getId());
        assertThat(successResponse.getResponse().getBody().isAccepted()).isFalse();
        assertThat(successResponse.getResponse().getMessage()).isEqualTo("You have been successfully unsubscribed");
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }
}