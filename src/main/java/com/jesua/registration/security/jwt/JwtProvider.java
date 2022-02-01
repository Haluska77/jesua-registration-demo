package com.jesua.registration.security.jwt;

import com.jesua.registration.config.Oauth2Properties;
import com.jesua.registration.security.exception.JsonExceptionHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final Oauth2Properties oauth2Properties;
    private final JsonExceptionHandler jsonExceptionHandler;
    private final Map< String, Authentication> cache = new HashMap<>();

    public String generateJwtToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + oauth2Properties.getJwtExpiration() * 1000))
                .signWith(SignatureAlgorithm.HS512, oauth2Properties.getJwtSecret())
                .compact();
    }

    public String registerTokenForEmail(Authentication authentication, String email) {
        String token = generateJwtToken(email);
        cache.put( token, authentication );
        return token;
    }

    public Authentication getAuth( String token ) {
        return cache.getOrDefault( token, null );
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(oauth2Properties.getJwtSecret()).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            jsonExceptionHandler.handleSignatureToken(new MalformedJwtException("Invalid JWT token"));
        } catch (SignatureException e) {
            jsonExceptionHandler.handleSignatureToken(new SignatureException("Invalid JWT signature"));
        } catch (ExpiredJwtException e) {
            jsonExceptionHandler.handleSignatureToken(new SignatureException("Expired JWT token"));
        } catch (UnsupportedJwtException e) {
            jsonExceptionHandler.handleSignatureToken(new UnsupportedJwtException("Unsupported JWT token"));
        } catch (IllegalArgumentException e) {
            jsonExceptionHandler.handleSignatureToken(new UnsupportedJwtException("JWT claims string is empty"));
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
        return Jwts.parser().setSigningKey(oauth2Properties.getJwtSecret()).parseClaimsJws(token).getBody();
    }
}
