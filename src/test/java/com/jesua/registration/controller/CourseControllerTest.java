package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.ErrorDTO;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseResponseDtoFromEntity;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerTest extends BaseControllerTest {

    @Autowired
    private CourseRepository courseRepository;

    private static User user;
    private int createdCourseId;

    @BeforeAll
    static void createUser(@Autowired UserRepository userRepository,
                           @Autowired CourseRepository courseRepository,
                           @Autowired ProjectRepository projectRepository){

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        user = buildUserWithOutId(project);
        userRepository.save(user);

        CourseDto courseDto = buildCourseDto(user.getId());
        Course course1 = buildCourseFromDto(courseDto, user);
        courseRepository.save(course1);
        Course course2 = buildCourseFromDto(courseDto, user);
        course2.setOpen(false);
        courseRepository.save(course2);
    }

    @AfterEach
    public void tearDown() {
        if(createdCourseId != 0) {
            courseRepository.deleteById(createdCourseId);
        }

    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired CourseRepository courseRepository,
                        @Autowired ProjectRepository projectRepository){

        courseRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSuccessfulEventsTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/events/eventList"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<CourseResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(2);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(2);

    }

    @Test
    @WithMockUser(roles = "USER")
    void accessDeniedTest() throws Exception {

        String contentAsString = mockMvc
                .perform(get("/events/eventList"))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo("Access is denied");
    }

    @Test
    void getUnauthorizedEventsTest() throws Exception {

        String contentAsString = mockMvc
                .perform(get("/events/eventList"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }

    @Test
    void getSuccessfulActiveEventsTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/events/activeEventList"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<CourseResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(1);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void deleteEvent() {
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSuccessfulEventTest() throws Exception {

        CourseDto courseDto = buildCourseDto(user.getId());
        Course course = buildCourseFromDto(courseDto, user);
        CourseResponseDto expectedCourseResponseDto = buildCourseResponseDtoFromEntity(course);

        MockHttpServletResponse response = mockMvc
                .perform(post("/events/addEvent")
                        .content(objectMapper.writeValueAsString(courseDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<CourseResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("id", "created", "createdBy.id", "createdBy.created", "createdBy.project.created").isEqualTo(expectedCourseResponseDto);
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        createdCourseId = successResponse.getResponse().getBody().getId();
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(expectedCourseResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getProject().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getProject().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void createUnauthorizedEventTest() throws Exception {

        CourseDto courseDto = buildCourseDto(user.getId());

        String contentAsString = mockMvc
                .perform(post("/events/addEvent")
                        .content(objectMapper.writeValueAsString(courseDto))
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
    void updateSuccessfulEventTest() throws Exception {

        //save course
        CourseDto courseDto = buildCourseDto(user.getId());
        Course course = buildCourseFromDto(courseDto, user);
        courseRepository.save(course);
        createdCourseId = course.getId();

        // update Dto to send updated data from UI
        courseDto.setOpen(false);
        courseDto.setDescription("new Description");
        courseDto.setStartDate("2022-05-01T15:00");

        Course updatedCourse = buildCourseFromDto(courseDto, user);
        CourseResponseDto expectedCourseResponseDto = buildCourseResponseDtoFromEntity(updatedCourse);
        expectedCourseResponseDto.setId(course.getId());

        MockHttpServletResponse response = mockMvc
                .perform(post("/events/updateEvent/" + course.getId())
                        .content(objectMapper.writeValueAsString(courseDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<CourseResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("created", "createdBy.created", "createdBy.project.created").isEqualTo(expectedCourseResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(expectedCourseResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getProject().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getProject().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void updateUnauthorizedEventsTest() throws Exception {

        CourseDto courseDto = buildCourseDto(user.getId());
        Course course = buildCourseFromDto(courseDto, user);
        courseRepository.save(course);
        createdCourseId = course.getId();

        String contentAsString = mockMvc
                .perform(post("/events/updateEvent/" + course.getId())
                        .content(objectMapper.writeValueAsString(courseDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse<ErrorDTO<String>> errorResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        assertThat(errorResponse.getError().getMessage()).isEqualTo(AUTHENTICATION_IS_REQUIRED);

    }
}