package com.vanguard.backend.controller;

import com.vanguard.backend.service.FraudDetectLambdaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final FraudDetectLambdaService lambdaService;

    @GetMapping("/analyse")
    public ResponseEntity<String> analyseUser() {
        String response = lambdaService.invokeFraudDetection();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
