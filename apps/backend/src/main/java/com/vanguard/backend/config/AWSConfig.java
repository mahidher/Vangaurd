package com.vanguard.backend.config;

import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.lambda.LambdaClient;

public class AWSConfig {


    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.create();
    }

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.create();
    }
}
