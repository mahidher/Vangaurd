package com.vanguard.backend.exception;

public class LambdaServiceException extends RuntimeException {
    public LambdaServiceException(String message) {
        super(message);
    }
    
    public LambdaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 