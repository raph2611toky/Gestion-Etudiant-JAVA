package com.studentmanagement.ui.auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.model.ResponsableResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RegisterFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField prenomField, nomField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private MainWindow mainWindow;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(236, 240, 241);
    private Color successColor = new Color(34, 197, 94);
    private Color errorColor = new Color(239, 68, 68);
    private static final String REGISTER_API = "http://localhost:8080/api/responsables/register";
    private RestTemplate restTemplate = new RestTemplate();

    public RegisterFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            System.out.println("Request URL: " + request.getURI());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Request Headers: " + request.getHeaders());
            return execution.execute(request, body);
        });

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
        JButton loginButton = createStyledButton("Se connecter", new Color(52, 73, 94));

        registerButton.addActionListener(_ -> {
            String prenom = prenomField.getText().trim();
            String nom = nomField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showErrorNotification("Veuillez remplir tous les champs.");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@.+\\..+$")) {
                showErrorNotification("Email invalide.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showErrorNotification("Les mots de passe ne correspondent pas.");
                return;
            }

            try {
                RegisterRequest request = new RegisterRequest();
                request.setPrenom(prenom);
                request.setNom(nom);
                request.setEmail(email);
                request.setMotDePasse(password);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

                ResponsableResponse response = restTemplate.postForObject(REGISTER_API, entity, ResponsableResponse.class);
                if (response != null) {
                    showSuccessNotification("Inscription réussie ! Veuillez vous connecter.");
                    mainWindow.showPanel("Login");
                } else {
                    showErrorNotification("Erreur lors de l'inscription.");
                }
            } catch (HttpClientErrorException ex) {
                showErrorNotification("Erreur lors de l'inscription : " + ex.getMessage());
            } catch (Exception ex) {
                showErrorNotification("Erreur inattendue : " + ex.getMessage());
            }
        });

        loginButton.addActionListener(_ -> mainWindow.showPanel("Login"));

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(loginButton);

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
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
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

    private void showSuccessNotification(String message) {
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                g2d.setColor(successColor);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel messageLabel = new JLabel("✅ " + message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);

        panel.add(messageLabel, BorderLayout.CENTER);
        toast.add(panel);
        toast.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(screenSize.width - toast.getWidth() - 30, 30);

        toast.setVisible(true);

        Timer timer = new Timer(3000, _ -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showErrorNotification(String message) {
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                g2d.setColor(errorColor);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel messageLabel = new JLabel("❌ " + message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);

        panel.add(messageLabel, BorderLayout.CENTER);
        toast.add(panel);
        toast.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(screenSize.width - toast.getWidth() - 30, 30);

        toast.setVisible(true);

        Timer timer = new Timer(3000, _ -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    static class RegisterRequest {
        private String prenom;
        private String nom;
        private String email;
        private String motDePasse;

        public RegisterRequest() {}

        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    }
}