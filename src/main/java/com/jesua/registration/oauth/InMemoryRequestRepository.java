package com.jesua.registration.oauth;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final Map<String, OAuth2AuthorizationRequest> cache = new HashMap<>();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
        String state = httpServletRequest.getParameter("state");
        if (state != null) {
            return  removeAuthorizationRequest(httpServletRequest);
        }
        return null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest,
                                         HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse) {
        String state = oAuth2AuthorizationRequest.getState();
        cache.put(state, oAuth2AuthorizationRequest);

    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest) {
        String state = httpServletRequest.getParameter("state");
        if (state != null){
            return cache.remove(state);
        }
        return null;
    }
}
