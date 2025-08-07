package com.vanguard.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.vanguard.backend.exception.LambdaServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class FraudDetectLambdaService {

    @Value("${aws.lambda.function}")
    private String functionName;
    
    @Value("${aws.lambda.region}")
    private String region;
    
    @Value("${aws.lambda.accessKey}")
    private String accessKey;
    
    @Value("${aws.lambda.secretKey}")
    private String secretKey;

    public String invokeFraudDetection() {
        log.info("Starting fraud detection analysis using Lambda function: {} in region: {}", functionName, region);
        
        
        AWSLambda lambda = null;
        try {
            // Initialize Lambda client with explicit credentials
            log.debug("Initializing AWS Lambda client with explicit credentials for region: {}", region);
            lambda = createLambdaClient();
            
            // Create invoke request
            InvokeRequest request = new InvokeRequest()
                    .withFunctionName(functionName);
            
            log.debug("Invoking Lambda function: {}", functionName);
            
            // Invoke the Lambda function
            InvokeResult result = lambda.invoke(request);
            
            // Check for function errors
            if (result.getFunctionError() != null) {
                String errorMessage = String.format("Lambda function returned error: %s", result.getFunctionError());
                log.error(errorMessage);
                throw new LambdaServiceException(errorMessage);
            }
            
            // Check status code
            if (result.getStatusCode() != 200) {
                String errorMessage = String.format("Lambda function returned non-success status code: %d", result.getStatusCode());
                log.error(errorMessage);
                throw new LambdaServiceException(errorMessage);
            }
            
            // Extract response payload
            String response = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            log.info("Successfully completed fraud detection analysis. Response length: {} characters", response.length());
            log.debug("Lambda response: {}", response);
            
            return response;
            
        } catch (AmazonServiceException e) {
            String errorMessage = String.format("AWS service error during Lambda invocation: %s (Error Code: %s)", 
                    e.getMessage(), e.getErrorCode());
            log.error(errorMessage, e);
            throw new LambdaServiceException(errorMessage, e);
            
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected error during Lambda invocation: %s", e.getMessage());
            log.error(errorMessage, e);
            throw new LambdaServiceException(errorMessage, e);
            
        } finally {
            // Clean up resources
            if (lambda != null) {
                try {
                    lambda.shutdown();
                    log.debug("AWS Lambda client shutdown completed");
                } catch (Exception e) {
                    log.warn("Error during Lambda client shutdown: {}", e.getMessage());
                }
            }
        }
    }
    
    
    private AWSLambda createLambdaClient() {
        try {
            // Create AWS credentials
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            
            // Parse region
            Regions awsRegion = Regions.fromName(region);
            
            // Build Lambda client with explicit credentials and region
            return AWSLambdaClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(awsRegion)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("Invalid AWS region specified: %s", region);
            log.error(errorMessage, e);
            throw new LambdaServiceException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to create AWS Lambda client: %s", e.getMessage());
            log.error(errorMessage, e);
            throw new LambdaServiceException(errorMessage, e);
        }
    }
}
