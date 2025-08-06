package com.vanguard.backend.service;

import com.vanguard.backend.entity.User;
import com.vanguard.backend.model.UserSummary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService{

    User createUser(User user);

    List<User> getAllUser();

    User getUserByUserName(String userName);

    Optional<User> updateUser(String userName, User user);

    String deleteUser(String userName);

    UserSummary getUserSummary(String userName);

}
