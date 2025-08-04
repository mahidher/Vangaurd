package com.vanguard.backend.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.vanguard.backend.entity.User;
import com.vanguard.backend.exception.UserException;
import com.vanguard.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public  class UserServiceImpl implements UserService{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private UserRepository userRepo;

    @Override
    public User createUser(User user) {
        if (ObjectUtils.isEmpty(user)) {
            throw new UserException("User details cannot be null");
        }
        dynamoDBMapper.save(user);
        return user;
    }

    @Override
    public List<User> getAllUser() {
            List<User> userList = dynamoDBMapper.scan(User.class, new DynamoDBScanExpression());
            return userList;
        }

    @Override
    public User getUserById(String id) {
        return dynamoDBMapper.load(User.class, id);
    }

    public Optional<User> updateUser(String userId, User updatedUser) {
        return userRepo.findById(userId).map(existing -> {
        existing.setUserId(updatedUser.getUserId());
        existing.setUserName(updatedUser.getUserName());
        return userRepo.save(existing);
    });
}

    private DynamoDBSaveExpression buildExpression(User user) {
        DynamoDBSaveExpression dynamoDBSaveExpression = new DynamoDBSaveExpression();
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("id", new ExpectedAttributeValue(new AttributeValue().withS(user.getUserId())));
        dynamoDBSaveExpression.setExpected(expectedAttributeValueMap);
        return dynamoDBSaveExpression;
    }

    @Override
    public User deleteuser(String id) {
        if (!StringUtils.hasLength(id)) {
            throw new UserException("User id cannot be null");
        }
        User user = dynamoDBMapper.load(User.class, id);
        if (ObjectUtils.isEmpty(user)) {
            throw new UserException("No data found");
        }
        dynamoDBMapper.delete(user);
        return user;
    }
}



