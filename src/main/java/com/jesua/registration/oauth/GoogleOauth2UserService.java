package com.jesua.registration.oauth;

import com.jesua.registration.entity.User;
import com.jesua.registration.repository.UserRepository;
import com.jesua.registration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleOauth2UserService extends DefaultOAuth2UserService {

    @Value("${user.default-avatar}")
    private String defaultAvatar;

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) throws Exception {

        Optional<User> userOptional = userRepository.findByEmail(oAuth2User.getAttribute("email"));

        if(userOptional.isPresent()) {
            if (!userOptional.get().getActive()) {
                throw new Exception("Account is not active");
            }
            updateOauthUser(userOptional.get());
        } else {
            registerOauthUser(oAuth2User);
        }

        return oAuth2User;
    }

    private User registerOauthUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setUserName(oAuth2User.getAttribute("name"));
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setRole("ROLE_MODERATOR");
        user.setActive(true);
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setAvatar(defaultAvatar);

        return userRepository.save(user);
    }

    private User updateOauthUser(User existingUser) {
        existingUser.setAuthProvider(AuthProvider.GOOGLE);
        return userRepository.save(existingUser);
    }
}
