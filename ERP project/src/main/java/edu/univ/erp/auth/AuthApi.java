package edu.univ.erp.auth;

import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.AuthService;

public class AuthApi {

    private final AuthService authService;

    public AuthApi() {
        // Create the authentication service when this API is made
        this.authService = new AuthService();
    }

    // Let user log in with username and password
    public UserSession login(String username, String password) {
        return authService.login(username, password);
    }

    // Allow user to change their password if they know the old one
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        return authService.changePassword(userId, oldPassword, newPassword);
    }
}