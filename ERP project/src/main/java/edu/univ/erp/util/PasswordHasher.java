package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    // Main method to test password hashing - for development use only
    public static void main(String[] args) {
        String plainPassword = "pass123";
        
        // Create secure hash of the password
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        
        // Print original and hashed password for testing
        System.out.println("Password: " + plainPassword);
        System.out.println("Hashed:   " + hashedPassword);
    }
}