package com.jesua.registration.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class AwsConfig {

    @Autowired
    protected AwsProperties awsProperties;

    @Bean
    public AmazonS3 AwsClient() {
        AWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey());

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(awsProperties.isPathStyleAccessEnabled())
                .withCredentials(new AWSStaticCredentialsProvider(credentials));

        if (StringUtils.hasText(awsProperties.getEndpoint())) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsProperties.getEndpoint(), awsProperties.getRegion()));
        } else {
            builder.withRegion(awsProperties.getRegion());
        }

        return builder.build();
    }
}
