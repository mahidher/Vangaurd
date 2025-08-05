package com.vanguard.backend.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    
    private String transactionId;
    private String fromUserId;
    private String toUserId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String description;
    private String status;
} 