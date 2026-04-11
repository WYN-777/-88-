package com.bupt.tasystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TA Recruitment System ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // consume newline

        if (choice == 1) {
            // Register
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter role (TA/MO/ADMIN): ");
            String role = scanner.nextLine();

            boolean success = AuthService.register(username, password, role);
            if (success) {
                System.out.println("Registration successful! You can now login.");
            } else {
                System.out.println("Registration failed. Username may already exist.");
            }

        } else if (choice == 2) {
            // Login
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User user = AuthService.login(username, password);

            if (user != null) {
                System.out.println("Login successful!");
                System.out.println("Role: " + user.getRole());

                // Show menu based on role
                if (user.getRole().equals("TA")) {
                    System.out.println("\n=== TA Menu ===");
                    System.out.println("1. View available jobs");
                    System.out.println("2. Apply for a job");
                    System.out.println("3. Check application status");
                    System.out.println("Other functions will be added later.");
                } else if (user.getRole().equals("MO")) {
                    System.out.println("\n=== MO Menu ===");
                    System.out.println("1. Post a job");
                    System.out.println("2. Select applicants");
                    System.out.println("Other functions will be added later.");
                } else if (user.getRole().equals("ADMIN")) {
                    System.out.println("\n=== Admin Menu ===");
                    System.out.println("1. Check TA workload");
                    System.out.println("Other functions will be added later.");
                }
            } else {
                System.out.println("Login failed. Wrong username or password.");
            }
        }

        scanner.close();
    }
}