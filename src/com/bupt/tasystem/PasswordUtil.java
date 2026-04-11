package com.bupt.tasystem;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    // Generate a random salt
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hash password with salt using SHA-256
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Password encryption failed", e);
        }
    }

    // Verify input password against stored hash and salt
    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        String hashOfInput = hashPassword(inputPassword, salt);
        return hashOfInput.equals(storedHash);
    }
}