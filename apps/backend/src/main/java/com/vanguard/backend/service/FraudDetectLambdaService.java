package com.vanguard.backend.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class FraudDetectLambdaService {

    @Value("${aws.lambda.function}")
    private String functionName;

    public String invokeFruadDetection(String userName){
        AWSLambda lambda = AWSLambdaClientBuilder.defaultClient();

        String payload = String.format("{\"userName\":\"%s\"}", userName);
        InvokeRequest request = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(payload);

        InvokeResult result = lambda.invoke(request);
        return new String(result.getPayload().array(), StandardCharsets.UTF_8);
    }
}
