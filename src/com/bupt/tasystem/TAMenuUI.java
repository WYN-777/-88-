package com.bupt.tasystem;

import javax.swing.*;
import java.awt.*;

public class TAMenuUI extends JFrame {

    public TAMenuUI() {
        setTitle("TA Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome TA! This is your dashboard.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton viewJobsButton = new JButton("View Available Jobs");
        JButton applyJobButton = new JButton("Apply for a Job");
        JButton checkStatusButton = new JButton("Check Application Status");

        buttonPanel.add(viewJobsButton);
        buttonPanel.add(applyJobButton);
        buttonPanel.add(checkStatusButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TAMenuUI().setVisible(true));
    }
}