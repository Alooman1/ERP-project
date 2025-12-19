package edu.univ.erp.service;

import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.AuthCredentials;
import edu.univ.erp.domain.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.Duration;

public class AuthService {

    private static final int MAX_ATTEMPTS = 3; // Maximum login attempts allowed
    private static final int LOCKOUT_DURATION_MINUTES = 5; // How long to lock account after too many failures
    private final UserAuthDAO userAuthDAO;

    public AuthService() {
        this.userAuthDAO = new UserAuthDAO();
    }

    // Handle user login with security features
    public UserSession login(String username, String password) {
        // Find user by username
        AuthCredentials credentials = userAuthDAO.findUserByUsername(username);

        if (credentials == null) {
            System.out.println("Login failed: Invalid credentials provided.");
            return null;
        }

        int userId = credentials.getUserId();

        // Check if account is locked due to too many failed attempts
        Timestamp lockTime = userAuthDAO.getLockoutTime(userId);
        if (lockTime != null) {
            Instant lockInstant = lockTime.toInstant();
            Duration duration = Duration.between(lockInstant, Instant.now());
            long minutesElapsed = duration.toMinutes();

            // If still in lockout period, deny login
            if (minutesElapsed < LOCKOUT_DURATION_MINUTES) {
                long minutesRemaining = LOCKOUT_DURATION_MINUTES - minutesElapsed;
                System.out.println("Login failed: User " + username + " is locked. Remaining: " + minutesRemaining + " mins.");
                return new UserSession(-1, "LOCKED", "LOCKED:" + minutesRemaining);
            } else {
                // Lockout period over, reset the account
                userAuthDAO.resetAttemptsAndLockout(userId);
            }
        }

        // Check if password is correct
        if (BCrypt.checkpw(password, credentials.getPasswordHash())) {
            // Successful login - reset failed attempts
            userAuthDAO.resetAttemptsAndLockout(userId);

            System.out.println("Login success: User '" + username + "' authenticated.");
            return new UserSession(
                    userId,
                    credentials.getUsername(),
                    credentials.getRole()
            );
        }

        // Wrong password - increment failed attempts
        userAuthDAO.incrementAttempts(userId);
        int currentAttempts = userAuthDAO.getFailedAttempts(userId);

        // If too many failed attempts, lock the account
        if (currentAttempts >= MAX_ATTEMPTS) {
            userAuthDAO.lockAccount(userId);
            System.out.println("Login failed: User " + username + " reached " + MAX_ATTEMPTS + " attempts and is now LOCKED.");
            return new UserSession(-1, "LOCKED", "LOCKED:" + LOCKOUT_DURATION_MINUTES);
        }

        System.out.println("Login failed: Incorrect password for user '" + username + "'. Attempts remaining: " + (MAX_ATTEMPTS - currentAttempts));
        return null;
    }

    // Allow user to change their password
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Get current password hash from database
        String oldHash = userAuthDAO.getPasswordHash(userId);

        if (oldHash == null) {
            System.err.println("Password change failed: User not found.");
            return false;
        }

        // Verify old password is correct
        if (BCrypt.checkpw(oldPassword, oldHash)) {
            // Hash and save the new password
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            return userAuthDAO.updatePasswordHash(userId, newHash);

        } else {
            System.err.println("Password change failed: Incorrect old password.");
            return false;
        }
    }
}