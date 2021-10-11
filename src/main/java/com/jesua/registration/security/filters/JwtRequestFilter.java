package com.jesua.registration.security.filters;

import com.jesua.registration.security.jwt.JwtProvider;
import com.jesua.registration.security.services.UserAuthPrincipal;
import com.jesua.registration.security.services.UserAuthPrincipalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserAuthPrincipalService userAuthPrincipalService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        String tokenUsername;
        String token = null;
        Authentication authentication = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            token = authorizationHeader.substring(7);
            jwtProvider.validateJwtToken(token);
            authentication = jwtProvider.getAuth(token);
        }

        if (authentication != null) {
            if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
                tokenUsername = jwtProvider.extractUserName(token);
                UserAuthPrincipal userDetails = (UserAuthPrincipal) this.userAuthPrincipalService.loadUserByUsername(tokenUsername);
                if (jwtProvider.validateTokenAgainstUserName(token, userDetails)) {
                    authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                }
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
