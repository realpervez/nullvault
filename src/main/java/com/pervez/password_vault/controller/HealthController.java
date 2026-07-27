package com.pervez.password_vault.controller;

import com.pervez.password_vault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        long count = userRepository.count(); // touches the DB
        return ResponseEntity.ok("OK - " + count + " users");
    }
}