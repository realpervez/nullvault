package com.pervez.password_vault.service;

import com.pervez.password_vault.model.PasswordEntry;
import com.pervez.password_vault.model.User;
import com.pervez.password_vault.repository.PasswordEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // Encrypt the password before saving
        String encrypted = encryptionService.encrypt(plainPassword, masterPassword, user.getSalt());

        // Create the entry
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

        PasswordEntry entry = passwordEntryRepository.findByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        return encryptionService.decrypt(entry.getEncryptedPassword(), masterPassword, user.getSalt());
    }

    public void deleteEntry(User user, Long entryId) {
        passwordEntryRepository.deleteByIdAndUser(entryId, user);
    }

    public List<PasswordEntry> searchEntries(User user, String siteName) {
        return passwordEntryRepository.findByUserAndSiteNameContainingIgnoreCase(user, siteName);
    }

    public PasswordEntry updateEntry(User user, Long id, String siteName, String siteUrl,
                                     String usernameForSite, String plainPassword,
                                     String masterPassword) throws Exception {
        PasswordEntry entry = passwordEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        // Encrypt the password before saving
        String encrypted = encryptionService.encrypt(plainPassword, masterPassword, user.getSalt());

        entry.setSiteName(siteName);
        entry.setSiteUrl(siteUrl);
        entry.setUsernameForSite(usernameForSite);
        entry.setEncryptedPassword(encrypted);

        return passwordEntryRepository.save(entry);
    }
}