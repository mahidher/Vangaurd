package com.vanguard.backend.service;

import com.vanguard.backend.entity.User;
import com.vanguard.backend.exception.UserException;
import com.vanguard.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public  class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        try {
            log.info("Creating user: {}", user);
            if (ObjectUtils.isEmpty(user)) {
                log.error("User details cannot be null");
                throw new UserException("User details cannot be null");
            }
            
            // Generate userId if not provided (required for DynamoDB partition key)
            if (user.getUserId() == null || user.getUserId().isEmpty()) {
                user.setUserId(UUID.randomUUID().toString());
                log.info("Generated new user ID: {}", user.getUserId());
            }
            
            // Set default balance if not provided
            if (user.getBalance() == null) {
                user.setBalance(BigDecimal.ZERO);
                log.info("Set default balance: 0.00");
            }
            
            // Set creation timestamp
            user.setCreatedAt(LocalDateTime.now());
            log.info("Set creation timestamp: {}", user.getCreatedAt());
            
            userRepository.save(user);
            log.info("User created successfully with ID: {}", user.getUserId());
            return user;
        } catch (UserException e) {
            log.error("Validation error while creating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating user: {}", e.getMessage(), e);
            throw new UserException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUser() {
        try {
            log.info("Fetching all users");
            List<User> users = userRepository.findAll();
            log.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error while fetching all users: {}", e.getMessage(), e);
            throw new UserException("Failed to fetch users: " + e.getMessage());
        }
    }

    @Override
    public User getUserById(String id) {
        try {
            log.info("Fetching user by ID: {}", id);
            User user = userRepository.findById(id).orElseThrow(() -> {
                log.warn("User not found with ID: {}", id);
                return new UserException("User not found with ID: " + id);
            });
            log.info("User found: {}", user);
            return user;
        } catch (UserException e) {
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching user by ID {}: {}", id, e.getMessage(), e);
            throw new UserException("Failed to fetch user: " + e.getMessage());
        }
    }

    public Optional<User> updateUser(String userId, User updatedUser) {
        try {
            log.info("Updating user with ID: {}", userId);
            updatedUser.setUserId(userId);
            userRepository.save(updatedUser);
            log.info("User updated successfully: {}", updatedUser);
            return Optional.ofNullable(updatedUser);
        } catch (Exception e) {
            log.error("Error while updating user with ID {}: {}", userId, e.getMessage(), e);
            throw new UserException("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public String deleteUser(String id) {
        try {
            log.info("Deleting user with ID: {}", id);
            if (!StringUtils.hasLength(id)) {
                log.error("User ID cannot be null or empty");
                throw new UserException("User id cannot be null");
            }
            
            // Check if user exists before deleting
            if (userRepository.findById(id).isEmpty()) {
                log.warn("User not found with ID: {}", id);
                throw new UserException("User not found with ID: " + id);
            }
            
            userRepository.deleteById(id);
            log.info("User deleted successfully with ID: {}", id);
            return "User Deleted Successfully";
        } catch (UserException e) {
            log.error("Error while deleting user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with ID {}: {}", id, e.getMessage(), e);
            throw new UserException("Failed to delete user: " + e.getMessage());
        }
    }
}