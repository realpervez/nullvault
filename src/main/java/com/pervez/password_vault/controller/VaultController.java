package com.pervez.password_vault.controller;

import com.pervez.password_vault.dto.PasswordEntryResponse;
import com.pervez.password_vault.model.PasswordEntry;
import com.pervez.password_vault.model.User;
import com.pervez.password_vault.service.VaultService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vault")
public class VaultController {

    @Autowired
    private VaultService vaultService;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEntry(@RequestBody Map<String, String> request) {
        try {
            User user = getCurrentUser();
            PasswordEntry entry = vaultService.addEntry(
                    user,
                    request.get("siteName"),
                    request.get("siteUrl"),
                    request.get("usernameForSite"),
                    request.get("plainPassword"),
                    request.get("masterPassword")
            );
            return ResponseEntity.ok("Password saved with ID: " + entry.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listEntries() {
        try {
            User user = getCurrentUser();
            List<PasswordEntryResponse> entries = vaultService.getAllEntries(user)
                    .stream()
                    .map(PasswordEntryResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchEntries(@RequestParam String siteName) {
        try {
            User user = getCurrentUser();
            List<PasswordEntryResponse> entries = vaultService.searchEntries(user, siteName)
                    .stream()
                    .map(PasswordEntryResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/decrypt/{id}")
    public ResponseEntity<?> decryptEntry(@PathVariable Long id,
                                          @RequestBody Map<String, String> request) {
        try {
            User user = getCurrentUser();
            String plainPassword = vaultService.decryptEntry(user, id, request.get("masterPassword"));
            return ResponseEntity.ok("Decrypted password: " + plainPassword);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            vaultService.deleteEntry(user, id);
            return ResponseEntity.ok("Entry deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            User user = getCurrentUser();
            PasswordEntry entry = vaultService.updateEntry(
                    user,
                    id,
                    request.get("siteName"),
                    request.get("siteUrl"),
                    request.get("usernameForSite"),
                    request.get("plainPassword"),
                    request.get("masterPassword")
            );
            return ResponseEntity.ok("Password updated with ID: " + entry.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}