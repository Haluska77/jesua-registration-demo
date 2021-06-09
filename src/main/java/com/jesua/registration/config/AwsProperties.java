package com.jesua.registration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jesua.storage.s3")
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private boolean pathStyleAccessEnabled;
    private String bucket;
}
