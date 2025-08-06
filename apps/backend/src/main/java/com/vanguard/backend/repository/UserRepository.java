package com.vanguard.backend.repository;

import com.vanguard.backend.entity.User;
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
public class UserRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<User> userTable;

    @PostConstruct
    public void init() {
        userTable = enhancedClient.table("Users", TableSchema.fromBean(User.class));
    }

    public void save(User user) {
        userTable.putItem(user);
    }

    public Optional<User> findById(String userName) {
        var key = Key.builder().partitionValue(userName).build();
        try {
            return Optional.ofNullable(userTable.getItem(key));
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        }
    }

    public void deleteById(String userName) {
        var key = Key.builder().partitionValue(userName).build();
        userTable.deleteItem(key);
    }

    public List<User> findAll() {
        return userTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public DynamoDbTable<User> getTable() {
        return userTable;
    }
    
    public DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }
}
