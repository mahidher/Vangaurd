package com.vanguard.backend.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.vanguard.backend.entity.User;
import com.vanguard.backend.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final DynamoDBMapper dynamoDBMapper;

    @Override
    public User saveUser(User user) {
        if (ObjectUtils.isEmpty(user)) {
            throw new UserException("User details cannot be null");
        }
       dynamoDBMapper.save(user);
        return user;
    }


    @Override
    public String deleteUser(String id) {

        if (!StringUtils.hasLength(id)) {
            throw new UserException("User id cannot be null");
        }
        User user = dynamoDBMapper.load(User.class, id);
        if (ObjectUtils.isEmpty(user)) {
            throw new UserException("No data found");
        }
        dynamoDBMapper.delete(user);
        return "User Deleted Successfully";
    }
}
