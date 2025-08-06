package com.vanguard.backend.controller;

import com.vanguard.backend.entity.User;
import com.vanguard.backend.model.UserSummary;
import com.vanguard.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUser() {
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable("userName") String userName) {
        return new ResponseEntity<>(userService.getUserByUserName(userName), HttpStatus.OK);
    }

    @GetMapping("/{userName}/summary")
    public ResponseEntity<UserSummary> getUserSummary(@PathVariable("userName") String userName) {
        return new ResponseEntity<>(userService.getUserSummary(userName), HttpStatus.OK);
    }

    @PutMapping("/{userName}")
    public ResponseEntity<User> updateUser(@PathVariable String userName, @RequestBody User updatedUser) {
    return userService.updateUser(userName, updatedUser)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userName}")
    public ResponseEntity<String> deleteUser(@PathVariable("userName") String userName) {
        return new ResponseEntity<>(userService.deleteUser(userName), HttpStatus.OK);
    }
}
