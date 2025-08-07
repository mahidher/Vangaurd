package com.vanguard.backend.controller;


import com.vanguard.backend.service.AdminServiceImpl;
import com.vanguard.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyze")
@RequiredArgsConstructor
public class AdminController {

    private final AdminServiceImpl adminService;

    @PostMapping
    public ResponseEntity<?> analyze(@RequestParam String userName) {
        String payload = adminService.getUserPayload(userName);

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in DB");
        }

        String lambdaResult = adminService.analyzeFraud(userName, payload);
        return ResponseEntity.ok(lambdaResult);
    }


}
