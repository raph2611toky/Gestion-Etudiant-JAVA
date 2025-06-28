package com.studentmanagement.ui.auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.model.ResponsableResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RegisterFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField prenomField, nomField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private MainWindow mainWindow;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(236, 240, 241);
    private static final String REGISTER_API = "http://localhost:8080/api/responsables/register";
    private RestTemplate restTemplate = new RestTemplate();

    public RegisterFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(backgroundColor);

        JPanel formPanel = createFormPanel();
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new SoftBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(20, 30, 30, 30)));

        JLabel titleLabel = new JLabel("Inscription Responsable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(createFieldPanel("Prénom", prenomField = new JTextField()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createFieldPanel("Nom", nomField = new JTextField()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createFieldPanel("Email", emailField = new JTextField()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createFieldPanel("Mot de passe", passwordField = new JPasswordField()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createFieldPanel("Confirmer le mot de passe", confirmPasswordField = new JPasswordField()));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(350, 40));

        JButton registerButton = createStyledButton("S'inscrire", primaryColor);
        JButton backButton = createStyledButton("Retour", new Color(52, 73, 94));

        registerButton.addActionListener(_ -> {
            String prenom = prenomField.getText().trim();
            String nom = nomField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@.+\\..+$")) {
                JOptionPane.showMessageDialog(this, "Email invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                ResponsableRequest request = new ResponsableRequest();
                request.setPrenom(prenom);
                request.setNom(nom);
                request.setEmail(email);
                request.setMotDePasse(password);
                ResponsableResponse response = restTemplate.postForObject(REGISTER_API, request,
                        ResponsableResponse.class);
                if (response != null) {
                    mainWindow.showPanel("Login");
                    JOptionPane.showMessageDialog(this, "Inscription réussie ! Veuillez vous connecter.");
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (HttpClientErrorException ex) {
                JOptionPane.showMessageDialog(this, "Cet email est déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(_ -> mainWindow.showPanel("Login"));

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(backButton);

        panel.add(buttonPanel);
        return panel;
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(350, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 100, 100));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        field.setPreferredSize(new Dimension(300, 30));
        field.setMaximumSize(new Dimension(350, 30));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, secondaryColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        });

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldPanel.add(field);
        return fieldPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(150, 40));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    class ResponsableRequest {
        private String prenom;
        private String nom;
        private String email;
        private String motDePasse;

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMotDePasse() {
            return motDePasse;
        }

        public void setMotDePasse(String motDePasse) {
            this.motDePasse = motDePasse;
        }
    }
}