package com.jesua.registration.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.jesua.registration.config.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AwsService {

    private final AmazonS3 awsClient;
    private final AwsProperties awsProperties;

    byte[] getBytes(String keyName) {
        try {
            S3ObjectInputStream objectContent = awsClient.getObject(awsProperties.getBucket(), keyName).getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download image", e);
        }
    }

}
