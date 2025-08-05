package com.vanguard.backend.repository;

import com.vanguard.backend.entity.Transaction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<Transaction> transactionTable;

    @PostConstruct
    public void init() {
        transactionTable = enhancedClient.table("transactions", TableSchema.fromBean(Transaction.class));
    }

    public void save(Transaction transaction) {
        transactionTable.putItem(transaction);
    }

    public Optional<Transaction> findById(String transactionId) {
        var key = Key.builder().partitionValue(transactionId).build();
        try {
            return Optional.ofNullable(transactionTable.getItem(key));
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        }
    }

    public List<Transaction> findAll() {
        return transactionTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public DynamoDbTable<Transaction> getTable() {
        return transactionTable;
    }
} 