package com.vanguard.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Configuration
public class DynamoDBConfig {

    private static final Logger log = LoggerFactory.getLogger(DynamoDBConfig.class);

    private final AwsProperties awsProperties;

    public DynamoDBConfig(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    @Bean(destroyMethod = "close")
    public DynamoDbClient dynamoDbClient() {
        log.info("Creating DynamoDB client for region: {}", awsProperties.getRegion());
        
        var builder = DynamoDbClient.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsProperties.getAccessKey(),
                                        awsProperties.getSecretKey()
                                )
                        )
                );

        if (awsProperties.getEndpoint() != null && !awsProperties.getEndpoint().isEmpty()) {
            log.info("Using custom DynamoDB endpoint: {}", awsProperties.getEndpoint());
            builder.endpointOverride(URI.create(awsProperties.getEndpoint()));
        }

        DynamoDbClient client = builder.build();
        log.info("DynamoDB client created successfully");
        return client;
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    // // ✅ Test the DynamoDB connection after beans are initialized
    // @EventListener(ApplicationReadyEvent.class)
    // public void testDynamoConnection() {
    //     try (DynamoDbClient client = dynamoDbClient()) {
    //         var tables = client.listTables().tableNames();
    //         log.info("✅ Connected to DynamoDB. Tables: {}", tables);
    //     } catch (Exception e) {
    //         log.error("❌ Failed to connect to DynamoDB: {}", e.getMessage(), e);
    //     }
    // }
}
