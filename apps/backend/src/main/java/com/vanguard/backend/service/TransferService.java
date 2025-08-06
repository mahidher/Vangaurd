package com.vanguard.backend.service;

import com.vanguard.backend.entity.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {

    Transaction transferFunds(String fromUserName, String toUserName, BigDecimal amount, String description);

    Transaction transferFunds(String fromUserName, String toUserName, BigDecimal amount);

} 