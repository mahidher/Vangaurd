package com.vanguard.backend.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBalance {
    
    private String userName;
    private BigDecimal balance;
    private LocalDateTime lastUpdated;
} 