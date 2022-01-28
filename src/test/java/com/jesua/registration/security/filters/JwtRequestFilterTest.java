package com.jesua.registration.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesua.registration.dto.ProjectDto;
import com.jesua.registration.entity.Project;
import com.jesua.registration.entity.User;
import com.jesua.registration.repository.ProjectRepository;
import com.jesua.registration.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static com.jesua.registration.builder.ProjectBuilder.buildProjectDto;
import static com.jesua.registration.builder.ProjectBuilder.buildProjectFromDto;
import static com.jesua.registration.builder.UserBuilder.buildUserWithOutId;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtRequestFilterTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Value("${app.oauth2.jwt-secret}")
    private String jwtSecret;

    @Value("${app.oauth2.jwt-expiration}")
    private int jwtExpiration;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    public void jwtSuccessTest() throws Exception {

        ProjectDto projectDto = buildProjectDto();
        Project project = buildProjectFromDto(projectDto);
        projectRepository.save(project);

        User user = buildUserWithOutId();
        userRepository.save(user);

        String jwtToken = Jwts.builder()
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        mockMvc
                .perform(get("/users/")
                        .header("Authorization", "Bearer " + jwtToken)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse();

    }
}