package com.vanguard.backend.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    
    private String fromUserId;
    private String toUserId;
    private BigDecimal amount;
    private String description;
} 