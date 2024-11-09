package com.example.matcher.profileservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.access.key}")
    private String ACCESS_KEY;

    @Value("${aws.secret.key}")
    private String SECRET_KEY;
    private final String ENDPOINT = "https://s3.ru-1.storage.selcloud.ru";

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(URI.create(ENDPOINT))
                .region(Region.of("ru-1"))
                .build();
    }
}
