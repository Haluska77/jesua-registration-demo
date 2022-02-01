package com.jesua.registration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.oauth2")
public class Oauth2Properties {
    private String authorizedRedirectUris;
    private String redirectUri;
    private String jwtSecret;
    private long jwtExpiration;
}
