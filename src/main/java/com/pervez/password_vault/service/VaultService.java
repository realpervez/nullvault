package com.pervez.password_vault.service;

import com.pervez.password_vault.model.PasswordEntry;
import com.pervez.password_vault.model.User;
import com.pervez.password_vault.repository.PasswordEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VaultService {

    @Autowired
    private PasswordEntryRepository passwordEntryRepository;

    @Autowired
    private EncryptionService encryptionService;

    public PasswordEntry addEntry(User user, String siteName, String siteUrl,
                                  String usernameForSite, String plainPassword,
                                  String masterPassword) throws Exception {

        if (siteName == null || siteName.isBlank()) {
            throw new RuntimeException("Site name is required");
        }
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new RuntimeException("Password cannot be empty");
        }
        if (masterPassword == null || masterPassword.isBlank()) {
            throw new RuntimeException("Master password is required to encrypt this entry");
        }

        String encrypted = encryptionService.encrypt(plainPassword, masterPassword, user.getSalt());

        PasswordEntry entry = new PasswordEntry();
        entry.setUser(user);
        entry.setSiteName(siteName);
        entry.setSiteUrl(siteUrl);
        entry.setUsernameForSite(usernameForSite);
        entry.setEncryptedPassword(encrypted);

        return passwordEntryRepository.save(entry);
    }

    public List<PasswordEntry> getAllEntries(User user) {
        return passwordEntryRepository.findByUser(user);
    }

    public String decryptEntry(User user, Long entryId, String masterPassword) throws Exception {

        if (masterPassword == null || masterPassword.isBlank()) {
            throw new RuntimeException("Master password is required to decrypt this entry");
        }

        PasswordEntry entry = passwordEntryRepository.findByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Entry not found or does not belong to you"));

        try {
            return encryptionService.decrypt(entry.getEncryptedPassword(), masterPassword, user.getSalt());
        } catch (Exception e) {
            throw new RuntimeException("Incorrect master password");
        }
    }

    @Transactional
    public void deleteEntry(User user, Long entryId) {
        PasswordEntry entry = passwordEntryRepository.findByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Entry not found or does not belong to you"));
        passwordEntryRepository.deleteByIdAndUser(entryId, user);
    }

    public List<PasswordEntry> searchEntries(User user, String siteName) {
        return passwordEntryRepository.findByUserAndSiteNameContainingIgnoreCase(user, siteName);
    }

    public PasswordEntry updateEntry(User user, Long id, String siteName, String siteUrl,
                                     String usernameForSite, String plainPassword,
                                     String masterPassword) throws Exception {

        if (masterPassword == null || masterPassword.isBlank()) {
            throw new RuntimeException("Master password is required to update this entry");
        }

        PasswordEntry entry = passwordEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Entry not found or does not belong to you"));

        String encrypted = encryptionService.encrypt(plainPassword, masterPassword, user.getSalt());

        entry.setSiteName(siteName);
        entry.setSiteUrl(siteUrl);
        entry.setUsernameForSite(usernameForSite);
        entry.setEncryptedPassword(encrypted);

        return passwordEntryRepository.save(entry);
    }
}