package com.vanguard.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl {


    private final DynamoDbClient dynamoDbClient;

    public String getUserPayload(String userName) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName("Transactions")
                .key(Map.of("userName", AttributeValue.fromS(userName)))
                .attributesToGet("payload")
                .build();

        var item = dynamoDbClient.getItem(request).item();

        return item != null ? item.get("payload").s() : null;
    }


    private final LambdaClient lambdaClient;

    public String analyzeFraud(String userName, String payloadJson) {
        String requestPayload = String.format("""
            {
                "userName": "%s",
                "payload": %s
            }
        """, userName, payloadJson); // Payload is already JSON string

        InvokeRequest request = InvokeRequest.builder()
                .functionName("fraudDetectionLambda") // Replace with your function name
                .payload(SdkBytes.fromUtf8String(requestPayload))
                .build();

        InvokeResponse response = lambdaClient.invoke(request);
        return response.payload().asUtf8String();
    }
}
