package com.jesua.registration.security.jwt;

import com.jesua.registration.security.exception.JsonExceptionHandler;
import com.jesua.registration.security.services.UserAuthPrincipal;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jesua.app.jwtSecret}")
    private String jwtSecret;

    @Value("${jesua.app.expiration}")
    private int jwtExpiration;

    private final JsonExceptionHandler jsonExceptionHandler;

    public String generateJwtToken(Authentication authentication) {

        UserAuthPrincipal userPrincipal = (UserAuthPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            jsonExceptionHandler.handleSignatureToken(new SignatureException("Invalid JWT signature"));
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            jsonExceptionHandler.handleSignatureToken(new SignatureException("Expired JWT token -> Message: {}", e));
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty -> Message: {}", e);
        }

        return false;
    }

    public Boolean validateTokenAgainstUserName(String token, UserDetails userDetails) {
        String userNameFromJwtToken = extractUserName(token);
        return (userNameFromJwtToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimTFunction.apply(claims);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}
