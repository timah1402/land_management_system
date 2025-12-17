package views.citizen.components;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable statistic card component
 */
public class StatCard extends JPanel {

    public StatCard(String title, String value, Color color) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(color);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(valueLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(titleLabel);
    }
}