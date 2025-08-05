package com.vanguard.backend.controller;

import com.vanguard.backend.entity.Transaction;
import com.vanguard.backend.model.TransferRequest;
import com.vanguard.backend.repository.TransactionRepository;
import com.vanguard.backend.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<Transaction> transferFunds(@RequestBody TransferRequest request) {
        try {
            log.info("Transfer request received: {} -> {}, amount: {}", 
                request.getFromUserId(), request.getToUserId(), request.getAmount());
            
            Transaction transaction = transferService.transferFunds(
                request.getFromUserId(),
                request.getToUserId(),
                request.getAmount(),
                request.getDescription()
            );
            
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("Transfer failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionRepository.findAll();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Failed to get transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String transactionId) {
        try {
            return transactionRepository.findById(transactionId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to get transaction {}: {}", transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

} 