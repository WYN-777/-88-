package com.bupt.tasystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginUI() {
        setTitle("TA Recruitment System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
        JLabel titleLabel = new JLabel("TA Recruitment System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        mainPanel.add(messageLabel, gbc);

        add(mainPanel);

        // Button actions
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterWindow();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        User user = AuthService.login(username, password);

        if (user != null) {
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Login successful! Welcome " + username);

            // Show TA menu if role is TA
            if (user.getRole().equals("TA")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Welcome " + username + "!\nRole: " + user.getRole() +
                                "\n\nDo you want to go to TA Menu?",
                        "Login Success",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    // Close login window and open TA menu
                    dispose();
                    new TAMenuUI().setVisible(true);
                    dispose();  // Close login window
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Welcome " + username + "!\nRole: " + user.getRole(),
                        "Login Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }

    private void openRegisterWindow() {
        new RegisterUI();
    }

    public static void main(String[] args) {
        // Run the login UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginUI().setVisible(true);
            }
        });
    }
}