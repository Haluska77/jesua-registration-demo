package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.CourseResponseDto;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.entity.UserProject;
import com.jesua.registration.exception.ErrorDto;
import com.jesua.registration.exception.ErrorResponse;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserProjectRepository;
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
import static com.jesua.registration.builder.UserProjectBuilder.buildUserProject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerTest extends BaseControllerTest {

    @Autowired
    private CourseRepository courseRepository;

    private static User user;
    private long createdCourseId;
    private static Project project;
    private static CourseDto courseDto;

    @BeforeAll
    static void createUser(@Autowired UserRepository userRepository,
                           @Autowired CourseRepository courseRepository,
                           @Autowired ProjectRepository projectRepository,
                           @Autowired UserProjectRepository userProjectRepository) {

        ProjectDto projectDto = buildProjectDto();
        project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        Project project2 = buildProjectFromDto(projectDto);
        projectRepository.save(project2);

        user = buildUserWithOutId();
        userRepository.save(user);

        courseDto = buildCourseDto(user.getId(), project.getId());
        Course course1 = buildCourseFromDto(courseDto, user, project);
        courseRepository.save(course1);

        UserProject userProject1 = buildUserProject(project, user);
        userProjectRepository.save(userProject1);
        UserProject userProject2 = buildUserProject(project2, user);
        userProjectRepository.save(userProject2);

        CourseDto courseDto2 = buildCourseDto(user.getId(), project2.getId());
        Course course2 = buildCourseFromDto(courseDto2, user, project2);
        course2.setOpen(false);
        courseRepository.save(course2);

    }

    @AfterEach
    public void tearDown() {
        if (createdCourseId != 0) {
            courseRepository.deleteById(createdCourseId);
        }

    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired CourseRepository courseRepository,
                        @Autowired ProjectRepository projectRepository,
                        @Autowired UserProjectRepository userProjectRepository) {

        userProjectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void getSuccessfulEventsWithoutFilteringTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/events/eventList"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<CourseResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(2);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(2);

    }

    @Test
    void getSuccessfulEventsOpenFilterTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/events/eventList?open=true"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<CourseResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(1);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    void getSuccessfulEventsProjectFilterTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/events/eventList?projects="+project.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<CourseResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(1);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSuccessfulEventTest() throws Exception {

        Course course = buildCourseFromDto(courseDto, user, project);
        CourseResponseDto expectedCourseResponseDto = buildCourseResponseDtoFromEntity(course);

        MockHttpServletResponse response = mockMvc
                .perform(post("/events/addEvent")
                        .content(objectMapper.writeValueAsString(courseDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<CourseResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("id", "created", "createdBy.id", "createdBy.created", "createdBy.projects", "project.created").isEqualTo(expectedCourseResponseDto);
        assertThat(successResponse.getResponse().getBody().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getCreated()).isNotNull();
        createdCourseId = successResponse.getResponse().getBody().getId();
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getId()).isNotNull();
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getCreated(), within(3, ChronoUnit.SECONDS));
//        assertThat(successResponse.getResponse().getBody().getCreatedBy().getProjects()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getProject().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getProject().getCreated()).isCloseTo(expectedCourseResponseDto.getProject().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void createUnauthorizedEventTest() throws Exception {

        String contentAsString = mockMvc
                .perform(post("/events/addEvent")
                        .content(objectMapper.writeValueAsString(courseDto))
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
    void updateSuccessfulEventTest() throws Exception {

        //save origCourse
        Course origCourse = buildCourseFromDto(courseDto, user, project);
        courseRepository.save(origCourse);
        createdCourseId = origCourse.getId();

        // update Dto to send updated data from UI
        courseDto.setOpen(false);
        courseDto.setDescription("new Description");
        courseDto.setStartDate("2022-05-01T15:00");

        Course updatedCourse = buildCourseFromDto(courseDto, origCourse, user, project);
        CourseResponseDto expectedCourseResponseDto = buildCourseResponseDtoFromEntity(updatedCourse);

        MockHttpServletResponse response = mockMvc
                .perform(post("/events/updateEvent/" + origCourse.getId())
                        .content(objectMapper.writeValueAsString(courseDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<CourseResponseDto> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody()).usingRecursiveComparison()
                .ignoringFields("created", "createdBy.created", "createdBy.projects.created", "project.created").isEqualTo(expectedCourseResponseDto);
        assertThat(successResponse.getResponse().getBody().getCreated()).isCloseTo(expectedCourseResponseDto.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getCreatedBy().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getCreated(), within(3, ChronoUnit.SECONDS));
//        assertThat(successResponse.getResponse().getBody().getCreatedBy().getProject().getCreated()).isCloseTo(expectedCourseResponseDto.getCreatedBy().getProject().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getBody().getProject().getCreated()).isCloseTo(expectedCourseResponseDto.getProject().getCreated(), within(3, ChronoUnit.SECONDS));
        assertThat(successResponse.getResponse().getMessage()).isNull();
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }

    @Test
    void updateUnauthorizedEventsTest() throws Exception {

        Course course = buildCourseFromDto(courseDto, user, project);
        courseRepository.save(course);
        createdCourseId = course.getId();

        String contentAsString = mockMvc
                .perform(post("/events/updateEvent/" + course.getId())
                        .content(objectMapper.writeValueAsString(courseDto))
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
    void getEventsByUserProjectTest() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/events/eventListByUserProject/"+user.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<CourseResponseDto>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().size()).isEqualTo(2);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(2);

    }
}