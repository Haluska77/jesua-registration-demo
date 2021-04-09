package com.jesua.registration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesua.registration.dto.CourseDto;
import com.jesua.registration.dto.FollowerDto;
import com.jesua.registration.dto.UserDto;
import com.jesua.registration.entity.Course;
import com.jesua.registration.entity.Follower;
import com.jesua.registration.entity.User;
import com.jesua.registration.exception.SuccessResponse;
import com.jesua.registration.repository.CourseRepository;
import com.jesua.registration.repository.FollowerRepository;
import com.jesua.registration.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.jesua.registration.builder.CourseBuilder.buildCourseDto;
import static com.jesua.registration.builder.CourseBuilder.buildCourseFromDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerDto;
import static com.jesua.registration.builder.FollowerBuilder.buildFollowerFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserDto;
import static com.jesua.registration.builder.UserBuilder.buildUserFromDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository,
                        @Autowired CourseRepository courseRepository,
                        @Autowired FollowerRepository followerRepository){

        followerRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getStatisticsTest() throws Exception {

        UserDto userDto = buildUserDto();
        User user = buildUserFromDto(userDto);
        userRepository.save(user);

        CourseDto courseDto = buildCourseDto(user.getId());
        Course course = buildCourseFromDto(courseDto);
        courseRepository.save(course);

        FollowerDto followerDto = buildFollowerDto(course.getId());
        Follower follower1 = buildFollowerFromDto(followerDto, course);
        follower1.setAccepted(true);
        followerRepository.save(follower1);

        Follower follower2 = buildFollowerFromDto(followerDto, course);
        follower2.setToken("dd6fg513DFS5d12df3DFd52");
        follower2.setAccepted(false);
        followerRepository.save(follower2);

        MockHttpServletResponse response = mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        SuccessResponse<List<Map<String, Object>>> successResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(successResponse.getResponse().getBody()).isNotNull();
        assertThat(successResponse.getResponse().getBody().get(0).get("active")).isEqualTo(1);
        assertThat(successResponse.getResponse().getBody().get(0).get("waiting")).isEqualTo(1);
        assertThat(successResponse.getResponse().getLength()).isEqualTo(1);
    }
}