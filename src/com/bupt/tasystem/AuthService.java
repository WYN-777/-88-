package com.bupt.tasystem;

public class AuthService {

    // Login verification
    public static User login(String username, String password) {
        // Find user by username
        User user = UserFileUtil.findUserByUsername(username);

        // User not found
        if (user == null) {
            System.out.println("User not found: " + username);
            return null;
        }

        // Verify password
        boolean isValid = PasswordUtil.verifyPassword(
                password,
                user.getPasswordHash(),
                user.getSalt()
        );

        if (isValid) {
            System.out.println("Login successful! Welcome " + username);
            return user;
        } else {
            System.out.println("Wrong password for user: " + username);
            return null;
        }
    }

    // Register a new user
    public static boolean register(String username, String password, String role) {
        // Check if user already exists
        User existingUser = UserFileUtil.findUserByUsername(username);
        if (existingUser != null) {
            System.out.println("Username already exists: " + username);
            return false;
        }

        // Create new user with encrypted password
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);
        User newUser = new User(username, passwordHash, salt, role);

        // Save to file
        UserFileUtil.addUser(newUser);
        System.out.println("Registration successful: " + username);
        return true;
    }
}