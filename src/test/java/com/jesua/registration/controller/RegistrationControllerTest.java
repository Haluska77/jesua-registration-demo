package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.FollowerEntityResponseDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.ErrorDto;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.FollowerRepository;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static com.jesua.registration.util.AppUtil.instantToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistrationControllerTest extends BaseControllerTest {

    @Autowired
    FollowerRepository followerRepository;

    private static User user;
    private static Course course;
    private static Follower follower2;
    private UUID createdFollowerId;
    private static Project project;

    @BeforeAll
    static void createUser(@Autowired UserRepository userRepository,
                           @Autowired CourseRepository courseRepository,
                           @Autowired FollowerRepository followerRepository,
                           @Autowired ProjectRepository projectRepository) {

        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        Project project2 = buildProjectFromDto(projectDto);
        projectRepository.save(project2);

        user = buildUserWithOutId();
        userRepository.save(user);

        CourseDto courseDto = buildCourseDto(user.getId(), project.getId());
        course = buildCourseFromDto(courseDto, user, project);
        courseRepository.save(course);

        CourseDto courseDto2 = buildCourseDto(user.getId(), project2.getId());
        Course course2 = buildCourseFromDto(courseDto2, user, project2);
        courseRepository.save(course2);

        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower follower1 = buildFollowerFromDto(followerDto, course);
        followerRepository.save(follower1);

        FollowerDto followerDto2 = buildFollowerDto(course2.getId());
        follower2 = buildFollowerFromDto(followerDto2, course2);
        follower2.setToken("dd6fg513DFS5d12df3DFd52");
        followerRepository.save(follower2);
    }

    @AfterEach
    public void tearDown() {
        if (createdFollowerId != null) {
            followerRepository.deleteById(createdFollowerId);
        }

    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired CourseRepository courseRepository,
                        @Autowired FollowerRepository followerRepository,
                        @Autowired ProjectRepository projectRepository) {

        followerRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void addFollowerSuccessfulTest() throws Exception {

        String responseMessage = "Tvoja registrácia na akciu (" + course.getDescription() + ", " + instantToString(course.getStartDate()) + ") prebehla úspešne! ";

        FollowerDto followerDto1 = buildFollowerDto(course.getId());

        MockHttpServletResponse response = mockMvc
                .perform(post("/registration/add")
                        .content(objectMapper.writeValueAsString(followerDto1))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<FollowerEntityResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        createdFollowerId = successResponse.getResponse().getBody().getId();
        assertThat(successResponse.getResponse().getBody().isAccepted()).isTrue();
        assertThat(successResponse.getResponse().getMessage()).startsWith(responseMessage);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void addFollowerMandatoryFieldTest() throws Exception {

        FollowerDto followerDto = new FollowerDto();
        followerDto.setEventId(2L);
        followerDto.setGdpr(true);

        MockHttpServletResponse response = mockMvc
                .perform(post("/registration/add")
                        .content(objectMapper.writeValueAsString(followerDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();


        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).contains("name nesmie byť null", "email nesmie byť null");

    }

    private static Stream<Arguments> mandatoryFields() {
        return Stream.of(
                Arguments.of("FollowerMismatchedInput.json", "Not valid input type has been inserted"),
                Arguments.of("FollowerInvalidParse.json", "Parsing JSON Error")
                );
    }

    @ParameterizedTest
    @MethodSource("mandatoryFields")
    void addFollowerMismatchedInputExceptionTest(String inputFile, String errorMessage) throws Exception {

        String resourceFile = "src/test/resources/"+inputFile;
        String jsonBody = readJsonFile(resourceFile);

        String response = mockMvc
                .perform(post("/registration/add")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();


        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(errorMessage);

    }

    @Test
    void getSuccessfulFollowersByProjectsTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/registration/?projects="+project.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<FollowerEntityResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(1);
        assertThat(successResponse.getResponse().getBody().get(0).getCourse().getId()).isEqualTo(course.getId());
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void getSuccessfulFollowersByTokenTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/registration/?token="+follower2.getToken()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<FollowerEntityResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(1);
        assertThat(successResponse.getResponse().getBody().get(0).getEmail()).isEqualTo(follower2.getEmail());
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Disabled("Handle different roles for filtered search")
    @Test
    void getUnauthorizedFollowersTest() throws Exception {

        String contentAsString = mockMvc
                .perform(get("/registration/?projects="+project.getId()))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDto<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
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

        SuccessResponse<FollowerEntityResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getId()).isEqualTo(follower2.getId());
        assertThat(successResponse.getResponse().getBody().isAccepted()).isFalse();
        assertThat(successResponse.getResponse().getMessage()).isEqualTo("Bol si úspešne odhlásený");
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void getSuccessfulOneFollowerByTokenTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/registration/token/"+follower2.getToken()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<FollowerEntityResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getEmail()).isEqualTo(follower2.getEmail());
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }
    public static String readJsonFile(String resourceFile) throws IOException {

        Path path = Paths.get(resourceFile);
        return String.join("", Files.readAllLines(path));
    }
}