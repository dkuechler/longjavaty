package io.github.dkuechler.longjavaty.users.controller;

import io.github.dkuechler.longjavaty.users.controller.dto.UserDataExport;
import io.github.dkuechler.longjavaty.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Controller for user account management operations.
 * Provides GDPR-compliant endpoints for user data management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GDPR Article 20 - Right to Data Portability
     * 
     * Exports all user data in JSON format including:
     * - User profile (ID, email, registration date)
     * - All workout records with heart rate data
     * - All health measurements
     * 
     * @param jwt The authenticated user's JWT token
     * @return JSON export of all user data
     */
    @GetMapping("/me/data")
    public ResponseEntity<UserDataExport> exportMyData(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        
        try {
            UserDataExport export = userService.exportUserData(userId);
            return ResponseEntity.ok(export);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    /**
     * GDPR Article 17 - Right to Erasure ("Right to be Forgotten")
     * 
     * Deletes all user data including:
     * - User account
     * - All workout records
     * - All measurements
     * - All workout heart rate samples
     * 
     * This operation is irreversible.
     * 
     * @param jwt The authenticated user's JWT token
     * @return 204 No Content if deletion successful, 404 if user not found
     */
    @DeleteMapping("/me/data")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        boolean deleted = userService.deleteUserAndAllData(userId);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
