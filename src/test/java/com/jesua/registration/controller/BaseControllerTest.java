package com.jesua.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ReactiveOAuth2ClientAutoConfiguration.class)
public class BaseControllerTest {

    final String AUTHENTICATION_IS_REQUIRED = "Full authentication is required to access this resource";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;
}
