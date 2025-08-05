package com.vanguard.backend.service;

import com.vanguard.backend.entity.Transaction;
import com.vanguard.backend.entity.User;
import com.vanguard.backend.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final DynamoDbEnhancedClient enhancedClient;

    public Transaction transferFunds(String fromUserId, String toUserId, BigDecimal amount, String description) {
        try {
            log.info("Starting fund transfer: {} -> {}, amount: {}", fromUserId, toUserId, amount);
            
            // Validate input
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new UserException("Transfer amount must be positive");
            }
            
            if (fromUserId.equals(toUserId)) {
                throw new UserException("Cannot transfer to the same user");
            }

            // Get table references
            DynamoDbTable<User> userTable = enhancedClient.table("user", TableSchema.fromBean(User.class));
            DynamoDbTable<Transaction> transactionTable = enhancedClient.table("transactions", TableSchema.fromBean(Transaction.class));

            // Fetch users
            Key fromUserKey = Key.builder().partitionValue(fromUserId).build();
            Key toUserKey = Key.builder().partitionValue(toUserId).build();
            
            User fromUser = userTable.getItem(fromUserKey);
            User toUser = userTable.getItem(toUserKey);

            // Validate users exist
            if (fromUser == null) {
                log.error("From user not found: {}", fromUserId);
                throw new UserException("From user not found: " + fromUserId);
            }
            if (toUser == null) {
                log.error("To user not found: {}", toUserId);
                throw new UserException("To user not found: " + toUserId);
            }

            // Validate sufficient balance
            if (fromUser.getBalance() == null || fromUser.getBalance().compareTo(amount) < 0) {
                log.error("Insufficient balance. User: {}, Balance: {}, Required: {}", 
                    fromUserId, fromUser.getBalance(), amount);
                throw new UserException("Insufficient balance");
            }

            // Calculate new balances
            BigDecimal newFromBalance = fromUser.getBalance().subtract(amount);
            BigDecimal newToBalance = toUser.getBalance().add(amount);
            
            fromUser.setBalance(newFromBalance);
            toUser.setBalance(newToBalance);

            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setTransactionId(UUID.randomUUID().toString());
            transaction.setFromUserId(fromUserId);
            transaction.setToUserId(toUserId);
            transaction.setAmount(amount);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setDescription(description != null ? description : 
                "Transfer from " + fromUserId + " to " + toUserId);

            log.info("Transaction created: {}", transaction.getTransactionId());
            log.info("From user new balance: {} -> {}", fromUser.getBalance().add(amount), newFromBalance);
            log.info("To user new balance: {} -> {}", toUser.getBalance().subtract(amount), newToBalance);

            // Execute atomic transaction
            TransactWriteItemsEnhancedRequest transactionRequest = TransactWriteItemsEnhancedRequest.builder()
                .addPutItem(userTable, fromUser)
                .addPutItem(userTable, toUser)
                .addPutItem(transactionTable, transaction)
                .build();

            enhancedClient.transactWriteItems(transactionRequest);
            
            log.info("Fund transfer completed successfully: {}", transaction.getTransactionId());
            return transaction;

        } catch (UserException e) {
            log.error("Transfer validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during fund transfer: {}", e.getMessage(), e);
            throw new UserException("Transfer failed: " + e.getMessage());
        }
    }

    public Transaction transferFunds(String fromUserId, String toUserId, BigDecimal amount) {
        return transferFunds(fromUserId, toUserId, amount, null);
    }
} 