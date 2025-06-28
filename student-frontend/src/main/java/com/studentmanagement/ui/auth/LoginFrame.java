package com.studentmanagement.ui.auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.model.ResponsableResponse;
import com.studentmanagement.service.StudentService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
// import org.springframework.http.client.ClientHttpRequestInterceptor;

public class LoginFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private MainWindow mainWindow;
    private final StudentService studentService;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(236, 240, 241);
    private static final String LOGIN_API = "http://localhost:8080/api/responsables/login";
    private static final String PROFILE_API = "http://localhost:8080/api/responsables/me";
    private RestTemplate restTemplate;

    public LoginFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.studentService = new StudentService();
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Configure RestTemplate with logging interceptor
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            System.out.println("Request URL: " + request.getURI());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Request Headers: " + request.getHeaders());
            return execution.execute(request, body);
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
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

        JLabel titleLabel = new JLabel("Connexion Responsable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(createFieldPanel("Email", emailField = new JTextField()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createFieldPanel("Mot de passe", passwordField = new JPasswordField()));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(350, 40));

        JButton loginButton = createStyledButton("Se connecter", primaryColor);
        JButton registerButton = createStyledButton("S'inscrire", new Color(52, 73, 94));

        loginButton.addActionListener(_ -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@.+\\..+$")) {
                JOptionPane.showMessageDialog(this, "Email invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                // Prepare login request
                LoginRequest request = new LoginRequest();
                request.setEmail(email);
                request.setMotDePasse(password);

                // Set headers for login request
                HttpHeaders loginHeaders = new HttpHeaders();
                loginHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(request, loginHeaders);

                // Perform login request
                LoginResponse loginResponse = restTemplate.postForObject(LOGIN_API, loginEntity, LoginResponse.class);
                if (loginResponse != null && loginResponse.getToken() != null) {
                    studentService.setJwtToken(loginResponse.getToken());

                    // Prepare profile request with Authorization header
                    HttpHeaders profileHeaders = new HttpHeaders();
                    profileHeaders.set("Authorization", "Bearer " + loginResponse.getToken());
                    HttpEntity<Void> profileEntity = new HttpEntity<>(profileHeaders);

                    // Perform profile request
                    ResponsableResponse profile = restTemplate.exchange(PROFILE_API, HttpMethod.GET, profileEntity, ResponsableResponse.class).getBody();
                    if (profile != null) {
                        profile.setToken(loginResponse.getToken());
                        mainWindow.setCurrentResponsable(profile);
                        mainWindow.showPanel("Dashboard");
                    } else {
                        JOptionPane.showMessageDialog(this, "Impossible de récupérer le profil.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HttpClientErrorException ex) {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la connexion : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(_ -> mainWindow.showPanel("Register"));

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(registerButton);

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

    // Make LoginRequest a static inner class
    static class LoginRequest {
        private String email;
        private String motDePasse;

        // No-args constructor for Jackson
        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    }

    // Make LoginResponse a static inner class
    static class LoginResponse {
        private String token;
        private String tokenType;
        private long expiresIn;

        // No-args constructor for Jackson
        public LoginResponse() {}

        // Constructor for convenience
        public LoginResponse(String token, String tokenType, long expiresIn) {
            this.token = token;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    }
}