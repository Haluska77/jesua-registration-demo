package com.jesua.registration.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesua.registration.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String email = ((DefaultOAuth2User) authentication.getPrincipal()).getAttribute("email");

        String token = jwtProvider.generateToken( authentication, email);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("accessToken", token)));
    }
}
