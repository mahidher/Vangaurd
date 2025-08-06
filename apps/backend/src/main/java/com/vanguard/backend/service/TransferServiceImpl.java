package com.vanguard.backend.service;

import com.vanguard.backend.entity.Transaction;
import com.vanguard.backend.entity.User;
import com.vanguard.backend.exception.UserException;
import com.vanguard.backend.repository.TransactionRepository;
import com.vanguard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction transferFunds(String fromUserName, String toUserName, BigDecimal amount, String description) {
        try {
            log.info("Starting fund transfer: {} -> {}, amount: {}", fromUserName, toUserName, amount);
            
            // Validate input
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new UserException("Transfer amount must be positive");
            }
            
            if (fromUserName.equals(toUserName)) {
                throw new UserException("Cannot transfer to the same user");
            }

            // Fetch users using repositories
            User fromUser = userRepository.findById(fromUserName).orElseThrow(() -> {
                log.error("From user not found: {}", fromUserName);
                return new UserException("From user not found: " + fromUserName);
            });
            
            User toUser = userRepository.findById(toUserName).orElseThrow(() -> {
                log.error("To user not found: {}", toUserName);
                return new UserException("To user not found: " + toUserName);
            });

            // Validate sufficient balance
            if (fromUser.getBalance() == null || fromUser.getBalance().compareTo(amount) < 0) {
                log.error("Insufficient balance. User: {}, Balance: {}, Required: {}", 
                    fromUserName, fromUser.getBalance(), amount);
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
            transaction.setFromUserName(fromUserName);
            transaction.setToUserName(toUserName);
            transaction.setAmount(amount);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setDescription(description != null ? description : 
                "Transfer from " + fromUserName + " to " + toUserName);

            log.info("Transaction created: {}", transaction.getTransactionId());
            log.info("From user new balance: {} -> {}", fromUser.getBalance().add(amount), newFromBalance);
            log.info("To user new balance: {} -> {}", toUser.getBalance().subtract(amount), newToBalance);

            // Execute atomic transaction using repository tables
            TransactWriteItemsEnhancedRequest transactionRequest = TransactWriteItemsEnhancedRequest.builder()
                .addPutItem(userRepository.getTable(), fromUser)
                .addPutItem(userRepository.getTable(), toUser)
                .addPutItem(transactionRepository.getTable(), transaction)
                .build();

            // Use the enhanced client from user repository to execute the atomic transaction
            userRepository.getEnhancedClient().transactWriteItems(transactionRequest);
            
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

    @Override
    public Transaction transferFunds(String fromUserName, String toUserName, BigDecimal amount) {
        return transferFunds(fromUserName, toUserName, amount, null);
    }
} 