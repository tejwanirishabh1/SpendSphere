
package com.spendsphere;

import javax.swing.*;
import java.awt.*;

public class SpendSphereApp {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color NAVY = new Color(24, 35, 56);
    private static final Color TEXT = new Color(40, 50, 65);
    private static final Color MUTED = new Color(115, 125, 140);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpendSphereApp::createUI);
    }

    private static void createUI() {
        JFrame frame = new JFrame("SpendSphere | Where Did My Money Go?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 720);
        frame.setMinimumSize(new Dimension(850, 600));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel brand = new JLabel("SpendSphere");
        brand.setFont(new Font("SansSerif", Font.BOLD, 26));
        brand.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("WHERE DID MY MONEY GO?");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tagline.setForeground(new Color(190, 200, 215));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.add(brand);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(tagline);

        JLabel status = new JLabel("PERSONAL FINANCE");
        status.setForeground(new Color(210, 220, 235));
        status.setFont(new Font("SansSerif", Font.BOLD, 12));

        header.add(brandPanel, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        // Main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(32, 35, 30, 35));

        JLabel welcome = new JLabel("Your money, in perspective.");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcome.setForeground(TEXT);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Track your spending, understand your habits, and plan with confidence."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        summaryPanel.setBackground(BACKGROUND);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        summaryPanel.setPreferredSize(new Dimension(1000, 135));

        summaryPanel.add(createSummaryCard("TOTAL SPENDING", "₹0.00", "No expenses recorded yet"));
        summaryPanel.add(createSummaryCard("THIS MONTH", "₹0.00", "Your monthly spending"));
        summaryPanel.add(createSummaryCard("TRANSACTIONS", "0", "Expenses recorded"));

        JPanel activityPanel = new JPanel(new BorderLayout(0, 12));
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 237)),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel activityTitle = new JLabel("Recent activity");
        activityTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        activityTitle.setForeground(TEXT);

        JLabel emptyMessage = new JLabel(
                "Your expense history will appear here when you add transactions.",
                SwingConstants.CENTER
        );
        emptyMessage.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emptyMessage.setForeground(MUTED);

        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(emptyMessage, BorderLayout.CENTER);
        activityPanel.setPreferredSize(new Dimension(1000, 230));
        activityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        content.add(welcome);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(28));
        content.add(summaryPanel);
        content.add(Box.createVerticalStrut(26));
        content.add(activityPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static JPanel createSummaryCard(String title, String value, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 237)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLabel.setForeground(MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        valueLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descriptionLabel.setForeground(MUTED);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(descriptionLabel);

        return card;
    }
}
