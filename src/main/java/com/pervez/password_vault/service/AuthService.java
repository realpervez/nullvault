package com.pervez.password_vault.service;

import com.pervez.password_vault.model.User;
import com.pervez.password_vault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    public User register(String username, String email, String masterPassword) throws Exception {

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email cannot be empty");
        }
        if (masterPassword == null || masterPassword.length() < 8) {
            throw new RuntimeException("Master password must be at least 8 characters");
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("This username is already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists");
        }

        String salt = encryptionService.generateSalt();
        String passwordHash = encryptionService.hashPassword(masterPassword, salt);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setMasterPasswordHash(passwordHash);
        user.setSalt(salt);

        return userRepository.save(user);
    }

    public User login(String username, String masterPassword) throws Exception {

        if (username == null || username.isBlank() || masterPassword == null || masterPassword.isBlank()) {
            throw new RuntimeException("Username and password are required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        boolean valid = encryptionService.verifyPassword(
                masterPassword,
                user.getMasterPasswordHash(),
                user.getSalt()
        );

        if (!valid) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }
}