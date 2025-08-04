package com.vanguard.backend.service;

import com.vanguard.backend.entity.User;

public interface UserService{


    User saveUser(User user);

    String updateUserDetails(String userId, User user);

    User getUserDeatils(String userId);

    String deleteUser(String userId);

}
