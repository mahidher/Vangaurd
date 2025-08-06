package com.vanguard.backend.service;

import com.vanguard.backend.entity.Transaction;
import com.vanguard.backend.entity.User;
import com.vanguard.backend.exception.UserException;
import com.vanguard.backend.model.UserSummary;
import com.vanguard.backend.repository.TransactionRepository;
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
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public  class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public User createUser(User user) {
        try {
            log.info("Creating user: {}", user);
            if (ObjectUtils.isEmpty(user)) {
                log.error("User details cannot be null");
                throw new UserException("User details cannot be null");
            }
            
            // Validate username is provided
            if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
                log.error("Username cannot be null or empty");
                throw new UserException("Username cannot be null or empty");
            }
            
            // Check if username already exists
            if (userRepository.findById(user.getUserName()).isPresent()) {
                log.error("Username already exists: {}", user.getUserName());
                throw new UserException("Username already exists: " + user.getUserName());
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
            log.info("User created successfully with username: {}", user.getUserName());
            return user;
        } catch (UserException e) {
            log.error("Validation error while creating user: {}", e.getMessage());
            throw new UserException(e.getMessage());
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
    public User getUserByUserName(String userName) {
        try {
            log.info("Fetching user by username: {}", userName);
            User user = userRepository.findById(userName).orElseThrow(() -> {
                log.warn("User not found with username: {}", userName);
                return new UserException("User not found with username: " + userName);
            });
            log.info("User found: {}", user);
            return user;
        } catch (UserException e) {
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching user by username {}: {}", userName, e.getMessage(), e);
            throw new UserException("Failed to fetch user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> updateUser(String currentUserName, User updatedUser) {
        try {
            log.info("Updating user with username: {}", currentUserName);
            
            // First, fetch the existing user to ensure it exists
            User existingUser = userRepository.findById(currentUserName).orElseThrow(() -> {
                log.warn("User not found with username: {}", currentUserName);
                return new UserException("User not found with username: " + currentUserName);
            });
            
            // Check if username is being changed
            String newUserName = updatedUser.getUserName();
            if (newUserName != null && !newUserName.equals(currentUserName)) {
                // Username is being changed - this requires delete and create
                log.info("Username change detected: {} -> {}", currentUserName, newUserName);
                
                // Check if new username already exists
                if (userRepository.findById(newUserName).isPresent()) {
                    log.error("New username already exists: {}", newUserName);
                    throw new UserException("Username already exists: " + newUserName);
                }
                
                // Update all related transactions BEFORE deleting the user
                updateTransactionsForUsernameChange(currentUserName, newUserName);
                
                // Delete old record
                userRepository.deleteById(currentUserName);
                log.info("Deleted old user record with username: {}", currentUserName);
                
                // Create new record with updated username
                updatedUser.setUserName(newUserName);
                // Preserve creation timestamp from original user
                updatedUser.setCreatedAt(existingUser.getCreatedAt());
            } else {
                // No username change - keep the current username
                updatedUser.setUserName(currentUserName);
                // Preserve creation timestamp
                updatedUser.setCreatedAt(existingUser.getCreatedAt());
            }
            
            // Save the updated user
            userRepository.save(updatedUser);
            log.info("User updated successfully: {}", updatedUser);
            return Optional.ofNullable(updatedUser);
        } catch (UserException e) {
            log.error("Validation error while updating user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error while updating user with username {}: {}", currentUserName, e.getMessage(), e);
            throw new UserException("Failed to update user: " + e.getMessage());
        }
    }

    private void updateTransactionsForUsernameChange(String oldUserName, String newUserName) {
        try {
            log.info("Updating transactions for username change: {} -> {}", oldUserName, newUserName);
            
            // Get all transactions involving the old username
            List<Transaction> sentTransactions = transactionRepository.findByFromUserName(oldUserName);
            List<Transaction> receivedTransactions = transactionRepository.findByToUserName(oldUserName);
            
            // Update sent transactions (fromUserName)
            for (Transaction transaction : sentTransactions) {
                transaction.setFromUserName(newUserName);
                transactionRepository.save(transaction);
                log.debug("Updated sent transaction {} with new fromUserName: {}", 
                    transaction.getTransactionId(), newUserName);
            }
            
            // Update received transactions (toUserName)
            for (Transaction transaction : receivedTransactions) {
                transaction.setToUserName(newUserName);
                transactionRepository.save(transaction);
                log.debug("Updated received transaction {} with new toUserName: {}", 
                    transaction.getTransactionId(), newUserName);
            }
            
            log.info("Successfully updated {} sent and {} received transactions for username change", 
                sentTransactions.size(), receivedTransactions.size());
                
        } catch (Exception e) {
            log.error("Error updating transactions for username change: {}", e.getMessage(), e);
            throw new UserException("Failed to update related transactions: " + e.getMessage());
        }
    }

    @Override
    public String deleteUser(String userName) {
        try {
            log.info("Deleting user with username: {}", userName);
            if (!StringUtils.hasLength(userName)) {
                log.error("Username cannot be null or empty");
                throw new UserException("Username cannot be null");
            }
            
            // Check if user exists before deleting
            if (userRepository.findById(userName).isEmpty()) {
                log.warn("User not found with username: {}", userName);
                throw new UserException("User not found with username: " + userName);
            }
            
            userRepository.deleteById(userName);
            log.info("User deleted successfully with username: {}", userName);
            return "User Deleted Successfully";
        } catch (UserException e) {
            log.error("Error while deleting user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with username {}: {}", userName, e.getMessage(), e);
            throw new UserException("Failed to delete user: " + e.getMessage());
        }
    }

    @Override
    public UserSummary getUserSummary(String userName) {
        try {
            log.info("Fetching user summary for username: {}", userName);
            
            // Get user details
            User user = getUserByUserName(userName);
            
            // Get transactions
            List<Transaction> sentTransactions = transactionRepository.findByFromUserName(userName);
            List<Transaction> receivedTransactions = transactionRepository.findByToUserName(userName);
            
            // Create combined transaction list with type flags
            List<UserSummary.TransactionWithType> allTransactions = new ArrayList<>();
            
            // Add sent transactions with SENT flag
            sentTransactions.forEach(transaction -> 
                allTransactions.add(new UserSummary.TransactionWithType(transaction, "SENT"))
            );
            
            // Add received transactions with RECEIVED flag
            receivedTransactions.forEach(transaction -> 
                allTransactions.add(new UserSummary.TransactionWithType(transaction, "RECEIVED"))
            );
            
            // Sort by timestamp (newest first)
            allTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));
            
            // Calculate totals
            BigDecimal totalAmountSent = sentTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            BigDecimal totalAmountReceived = receivedTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int totalTransactionCount = sentTransactions.size() + receivedTransactions.size();
            
            // Create and return user summary
            UserSummary userSummary = new UserSummary();
            userSummary.setUserName(user.getUserName());
            userSummary.setBalance(user.getBalance());
            userSummary.setCreatedAt(user.getCreatedAt());
            userSummary.setTransactions(allTransactions);
            userSummary.setTotalTransactionCount(totalTransactionCount);
            userSummary.setTotalAmountSent(totalAmountSent);
            userSummary.setTotalAmountReceived(totalAmountReceived);
            
            log.info("User summary created for {}: {} transactions, sent: {}, received: {}", 
                userName, totalTransactionCount, totalAmountSent, totalAmountReceived);
            
            return userSummary;
        } catch (UserException e) {
            log.error("Error while fetching user summary: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching user summary for {}: {}", userName, e.getMessage(), e);
            throw new UserException("Failed to fetch user summary: " + e.getMessage());
        }
    }
}