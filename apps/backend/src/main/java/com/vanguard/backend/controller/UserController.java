package com.vanguard.backend.controller;

import com.vanguard.backend.entity.User;
import com.vanguard.backend.repository.UserRepository;
import com.vanguard.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{id}")
    public User getUserDetails(@PathVariable("id") String userId) {
        return userService.getUserDeatils(userId);
    }

    @PutMapping("/{id}")
    public String updateUserDetails(@PathVariable("id") String userId, @RequestBody User user) {
        return userService.updateUserDetails(userId,user);
    }
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") String userId) {
        return userService.deleteUser(userId);
    }


}
