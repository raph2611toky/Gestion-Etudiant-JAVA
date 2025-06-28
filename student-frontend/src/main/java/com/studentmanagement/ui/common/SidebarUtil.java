package com.studentmanagement.ui.common;

import com.studentmanagement.model.ResponsableResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SidebarUtil {
    private static final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color HOVER_COLOR = new Color(70, 90, 110);

    public static JPanel createSidebar(MainWindow mainWindow, String activeMenu) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logoLabel = new JLabel("Student Manager");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebar.add(logoLabel);

        sidebar.add(createMenuButton("🏠 Dashboard", "Dashboard".equals(activeMenu), _ -> mainWindow.showPanel("Dashboard")));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("👥 Étudiants", "Students".equals(activeMenu), _ -> mainWindow.showPanel("Students")));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("📊 Notes", "Grades".equals(activeMenu), _ -> mainWindow.showPanel("Grades")));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("⚙️ Paramètres", "Settings".equals(activeMenu), _ -> mainWindow.showPanel("Settings")));

        sidebar.add(Box.createVerticalGlue());

        ResponsableResponse responsable = mainWindow.getCurrentResponsable();
        if (responsable != null) {
            JPanel profilePanel = new JPanel();
            profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
            profilePanel.setBackground(SIDEBAR_COLOR);
            profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            profilePanel.setMaximumSize(new Dimension(220, 60));
            profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            ImageIcon profileIcon = new ImageIcon(SidebarUtil.class.getResource("/images/default.png"));
            Image scaledImage = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel profileImage = new JLabel(new ImageIcon(scaledImage));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(SIDEBAR_COLOR);

            JLabel nameLabel = new JLabel(responsable.getPrenom() + " " + responsable.getNom());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(Color.WHITE);

            JLabel emailLabel = new JLabel(responsable.getEmail());
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emailLabel.setForeground(new Color(200, 200, 200));

            infoPanel.add(nameLabel);
            infoPanel.add(emailLabel);

            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem profileItem = new JMenuItem("Voir Profil");
            JMenuItem logoutItem = new JMenuItem("Déconnexion");
            profileItem.addActionListener(_ -> showProfileDialog(mainWindow, responsable));
            logoutItem.addActionListener(_ -> {
                mainWindow.setCurrentResponsable(null);
                mainWindow.showPanel("Login");
            });
            popupMenu.add(profileItem);
            popupMenu.add(logoutItem);

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            };

            profileImage.addMouseListener(mouseAdapter);
            infoPanel.addMouseListener(mouseAdapter);

            profilePanel.add(profileImage);
            profilePanel.add(Box.createRigidArea(new Dimension(10, 0)));
            profilePanel.add(infoPanel);

            sidebar.add(profilePanel);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        sidebar.add(createMenuButton("🚪 Déconnexion", false, _ -> {
            mainWindow.setCurrentResponsable(null);
            mainWindow.showPanel("Login");
        }));

        return sidebar;
    }

    private static JButton createMenuButton(String text, boolean isActive, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() && !isActive) {
                    g2d.setColor(HOVER_COLOR);
                } else {
                    g2d.setColor(getBackground());
                }
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(isActive ? PRIMARY_COLOR : SIDEBAR_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(action);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    button.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(isActive ? PRIMARY_COLOR : SIDEBAR_COLOR);
            }
        });

        return button;
    }

    private static void showProfileDialog(MainWindow mainWindow, ResponsableResponse responsable) {
        JDialog dialog = new JDialog(mainWindow, "Profil Utilisateur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainWindow);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Nom: " + responsable.getPrenom() + " " + responsable.getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);

        JLabel emailLabel = new JLabel("Email: " + responsable.getEmail());
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        contentPanel.add(emailLabel, gbc);

        JLabel idLabel = new JLabel("ID: " + responsable.getId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        contentPanel.add(idLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton closeButton = new JButton("Fermer") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(PRIMARY_COLOR.brighter());
                } else {
                    g2d.setColor(PRIMARY_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.addActionListener(_ -> dialog.dispose());

        buttonPanel.add(closeButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}