package com.studentmanagement.ui.dashboard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;

public class DashboardFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private MainWindow mainWindow;
    private Color primaryColor = new Color(41, 128, 185);
    private Color sidebarColor = new Color(52, 73, 94);
    private Color backgroundColor = new Color(236, 240, 241);
    private Color cardColor = Color.WHITE;

    public DashboardFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Use SidebarUtil to create the sidebar
        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Dashboard");
        add(sidebar, BorderLayout.WEST);

        // Contenu principal
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(sidebarColor);
        header.add(titleLabel, BorderLayout.WEST);

        mainContent.add(header, BorderLayout.NORTH);

        // Cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(backgroundColor);

        cardsPanel.add(createStatCard("👥 Total Étudiants", "0", primaryColor));
        cardsPanel.add(createStatCard("📚 Cours Actifs", "0", new Color(46, 204, 113)));
        cardsPanel.add(createStatCard("📋 Examens", "0", new Color(230, 126, 34)));
        cardsPanel.add(createStatCard("🎓 Diplômés", "0", new Color(155, 89, 182)));

        mainContent.add(cardsPanel, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                new SoftBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }
}