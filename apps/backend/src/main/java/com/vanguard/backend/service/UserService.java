package com.vanguard.backend.service;

import com.vanguard.backend.entity.User;

public interface UserService{


    User saveUser(User user);

   String deleteUser(String userId);
}
