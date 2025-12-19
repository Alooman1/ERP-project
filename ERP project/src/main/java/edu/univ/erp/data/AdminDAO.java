package edu.univ.erp.data;

import edu.univ.erp.domain.Admin; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Data Access Object for Admin-related operations and System Settings.
public class AdminDAO {

    // Creates a new Admin profile.
    public boolean createAdminProfile(int userId, String fullName) {
        String sql = "INSERT INTO admins (user_id, full_name) VALUES (?, ?)";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, fullName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetches Admin profile details.
    public edu.univ.erp.domain.Admin getAdminProfile(int userId) {
        String sql = "SELECT a.full_name, u.username " +
                "FROM admins a JOIN auth_db.users_auth u ON a.user_id = u.user_id " +
                "WHERE a.user_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new edu.univ.erp.domain.Admin(
                            userId,
                            rs.getString("full_name"),
                            rs.getString("username") 
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates Admin display name.
    public boolean updateAdminName(int userId, String newName) {
        String sql = "UPDATE admins SET full_name = ? WHERE user_id = ?";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lists all Admins in the system.
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT a.user_id, a.full_name, u.username " +
                "FROM admins a JOIN auth_db.users_auth u ON a.user_id = u.user_id " +
                "ORDER BY a.full_name";

        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                admins.add(new Admin(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    // Checks the global "Maintenance Mode" status from the 'settings' table.
    public boolean isMaintenanceModeOn() {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_on'";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return "true".equalsIgnoreCase(rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Updates the "Maintenance Mode" flag.
    public boolean setMaintenanceMode(boolean isNowOn) {
        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = 'maintenance_on'";
        try (Connection conn = DBConnectionManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.valueOf(isNowOn));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Deletes an Admin profile.
    public boolean deleteAdminProfile(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM admins WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        }
    }
}