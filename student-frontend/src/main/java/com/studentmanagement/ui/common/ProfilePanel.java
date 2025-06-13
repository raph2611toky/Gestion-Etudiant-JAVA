package com.studentmanagement.ui.common;

import com.studentmanagement.model.ResponsableResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private MainWindow mainWindow;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);

    public ProfilePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateProfile();
    }

    public void updateProfile() {
        removeAll();
        ResponsableResponse responsable = mainWindow.getCurrentResponsable();
        if (responsable != null) {
            ImageIcon profileIcon = new ImageIcon(getClass().getResource("/images/default.png"));
            Image scaledImage = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel profileImage = new JLabel(new ImageIcon(scaledImage));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(BACKGROUND_COLOR);

            JLabel nameLabel = new JLabel(responsable.getPrenom() + " " + responsable.getNom());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(PRIMARY_COLOR);

            JLabel emailLabel = new JLabel(responsable.getEmail());
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emailLabel.setForeground(new Color(100, 100, 100));

            infoPanel.add(nameLabel);
            infoPanel.add(emailLabel);

            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem profileItem = new JMenuItem("Voir Profil");
            JMenuItem logoutItem = new JMenuItem("Déconnexion");
            profileItem.addActionListener(_ -> showProfileDialog(responsable));
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

            add(infoPanel);
            add(Box.createRigidArea(new Dimension(10, 0)));
            add(profileImage);
        }
        revalidate();
        repaint();
    }

    private void showProfileDialog(ResponsableResponse responsable) {
        JDialog profileDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Profil Responsable", true);
        profileDialog.setLayout(new BorderLayout());
        profileDialog.setSize(400, 300);
        profileDialog.setLocationRelativeTo(this);

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