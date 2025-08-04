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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") String userId) {
        return userService.deleteUser(userId);
    }

    @PostMapping("/employee")
    public User saveEmployee(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
