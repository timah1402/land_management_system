package views.citizen.components;

import models.User;
import utils.SessionManager;
import utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable header panel for the Citizen Dashboard
 */
public class HeaderPanel extends JPanel {

    public HeaderPanel(Runnable logoutAction) {
        setLayout(new BorderLayout());
        setBackground(new Color(26, 188, 156));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(26, 188, 156));

        JLabel titleLabel = new JLabel("Citizen Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(26, 188, 156));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> logoutAction.run());

        rightPanel.add(userLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        rightPanel.add(logoutButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
}