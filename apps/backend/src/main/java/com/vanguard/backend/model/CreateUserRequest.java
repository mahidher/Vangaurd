package com.vanguard.backend.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    
    private String userName;
    private BigDecimal balance; // Optional - defaults to 0.00 if not provided
} 