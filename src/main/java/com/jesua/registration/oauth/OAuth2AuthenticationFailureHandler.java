package com.jesua.registration.oauth;

import com.jesua.registration.oauth.util.CookieUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jesua.registration.oauth.util.CookieUtils.deleteCookies;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REDIRECT_URI;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI).map(Cookie::getValue).orElse(("/"));

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl).queryParam("error",
                exception.getLocalizedMessage()).build().toUriString();

        deleteCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}