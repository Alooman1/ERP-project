package edu.univ.erp.data;

import edu.univ.erp.domain.AuthCredentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

// DAO for managing User Authentication and Credentials.
// Connects to the secure 'auth_db'.
public class UserAuthDAO {

    // Retrieves the hashed password for a user ID.
    public String getPasswordHash(int userId) {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id = ?";

        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates a user's password hash.
    public boolean updatePasswordHash(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Finds user details by username (used during login).
    // Returns credentials object including lockout status.
    public AuthCredentials findUserByUsername(String username) {
        String sql = "SELECT user_id, role, password_hash, failed_attempts, lockout_time FROM users_auth WHERE username = ? AND status = 'active'";

        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");
                    String passwordHash = rs.getString("password_hash");
                    int failedAttempts = rs.getInt("failed_attempts");
                    Timestamp lockoutTime = rs.getTimestamp("lockout_time");
                    // Note: AuthCredentials constructor might need update to hold attempts/lockout if used elsewhere,
                    // but core login logic usually just needs ID/Role/Hash here.
                    return new AuthCredentials(userId, username, role, passwordHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Creates a new user record in the Auth DB.
    // Returns the generated User ID.
    public int createUser(String username, String hashedPassword, String role) {
        String sql = "INSERT INTO users_auth (username, password_hash, role, status, failed_attempts) VALUES (?, ?, ?, 'active', 0)";
        int newUserId = -1;

        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 1) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        newUserId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newUserId;
    }
    
    // Deletes a user from the Auth DB.
    public boolean deleteUser(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM users_auth WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() == 1;
        }
    }

    // --- Account Lockout Features ---

    public int getFailedAttempts(int userId) {
        String sql = "SELECT failed_attempts FROM users_auth WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("failed_attempts") : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Timestamp getLockoutTime(int userId) {
        String sql = "SELECT lockout_time FROM users_auth WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getTimestamp("lockout_time") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Resets failed attempts counter and unlocks account (on successful login).
    public boolean resetAttemptsAndLockout(int userId) {
        String sql = "UPDATE users_auth SET failed_attempts = 0, lockout_time = NULL WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Increases the failed login counter.
    public boolean incrementAttempts(int userId) {
        String sql = "UPDATE users_auth SET failed_attempts = failed_attempts + 1 WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Locks the account by setting the lockout_time to NOW.
    public boolean lockAccount(int userId) {
        String sql = "UPDATE users_auth SET lockout_time = NOW() WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}