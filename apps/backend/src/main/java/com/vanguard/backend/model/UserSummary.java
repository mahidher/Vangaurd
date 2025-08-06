package com.vanguard.backend.model;

import com.vanguard.backend.entity.Transaction;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummary {
    
    private String userName;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private List<TransactionWithType> transactions;
    private int totalTransactionCount;
    private BigDecimal totalAmountSent;
    private BigDecimal totalAmountReceived;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionWithType {
        private String transactionId;
        private String fromUserName;
        private String toUserName;
        private BigDecimal amount;
        private LocalDateTime timestamp;
        private String description;
        private String type; // "SENT" or "RECEIVED"
        
        public TransactionWithType(Transaction transaction, String type) {
            this.transactionId = transaction.getTransactionId();
            this.fromUserName = transaction.getFromUserName();
            this.toUserName = transaction.getToUserName();
            this.amount = transaction.getAmount();
            this.timestamp = transaction.getTimestamp();
            this.description = transaction.getDescription();
            this.type = type;
        }
    }
} 