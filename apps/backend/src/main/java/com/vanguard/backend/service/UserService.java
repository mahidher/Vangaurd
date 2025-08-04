package com.vanguard.backend.service;

import com.vanguard.backend.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService{

    User createUser(User user);

    List<User> getAllUser();

    User getUserById(String id);

    Optional<User> updateUser(String userId, User user);

    User deleteuser(String id);



}