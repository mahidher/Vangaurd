package com.vanguard.backend.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.vanguard.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public String delete(String userId){
        User user = dynamoDBMapper.load(User.class, userId);
        dynamoDBMapper.delete(user);
        return "User Deleted Successfully";
    }

}
