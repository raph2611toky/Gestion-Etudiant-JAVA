package com.studentmanagement.ui.common;

import com.studentmanagement.model.ResponsableResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        sidebar.add(createMenuButton("📊 Statistiques", "Statistics".equals(activeMenu), null));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createMenuButton("⚙️ Paramètres", "Settings".equals(activeMenu), null));

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
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(isActive ? PRIMARY_COLOR : SIDEBAR_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 40));
        button.setPreferredSize(new Dimension(220, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        if (action != null) {
            button.addActionListener(action);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        return button;
    }

    private static void showProfileDialog(MainWindow mainWindow, ResponsableResponse responsable) {
        JDialog profileDialog = new JDialog(mainWindow, "Profil Responsable", true);
        profileDialog.setLayout(new BorderLayout());
        profileDialog.setSize(400, 300);
        profileDialog.setLocationRelativeTo(mainWindow);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Profil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(responsable.getPrenom()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(responsable.getNom()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(responsable.getEmail()), gbc);

        JButton closeButton = new JButton("Fermer");
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(_ -> profileDialog.dispose());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(closeButton, gbc);

        profileDialog.add(formPanel, BorderLayout.CENTER);
        profileDialog.setVisible(true);
    }
}