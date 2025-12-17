package views.agent.components;

import models.LandAgent;
import models.User;
import utils.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable header panel for the Agent Dashboard
 */
public class AgentHeaderPanel extends JPanel {

    public AgentHeaderPanel(LandAgent currentAgent, Runnable logoutAction) {
        setLayout(new BorderLayout());
        setBackground(new Color(41, 128, 185));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("Agent Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(41, 128, 185));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        JLabel userLabel = new JLabel("Agent: " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        if (currentAgent != null) {
            JLabel regionLabel = new JLabel(" | Region: " + currentAgent.getRegion());
            regionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            regionLabel.setForeground(Color.WHITE);
            rightPanel.add(userLabel);
            rightPanel.add(regionLabel);
        } else {
            rightPanel.add(userLabel);
        }

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> logoutAction.run());

        rightPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        rightPanel.add(logoutButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
}